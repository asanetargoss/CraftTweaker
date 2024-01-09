package com.blamejared.crafttweaker.gradle

import com.blamejared.gradle.mod.utils.GMUtils
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.plugins.ide.idea.model.IdeaModel
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class DefaultPlugin : Plugin<Project> {
    private lateinit var gametestLibrary: Configuration
    private lateinit var library: Configuration
    private lateinit var localOnlyRuntime: Configuration

    override fun apply(project: Project): Unit = project.run {

        setupDefaults(project)
        applyJavaPlugin(project)
        createConfigurations(project)
        applyIdeaPlugin(project)
        applyDependencies(project)
        applyMavenPlugin(project)
    }


    private fun setupDefaults(project: Project) {
        project.plugins.apply(BasePlugin::class.java)
        val base = project.extensions.getByType(BasePluginExtension::class.java)

        base.archivesName.set("${Properties.MOD_NAME}-${project.name.lowercase()}-${Versions.MINECRAFT}")
        project.version = GMUtils.updatingVersion(Versions.MOD)
        project.group = Properties.GROUP

        project.tasks.withType<GenerateModuleMetadata>().all {
            enabled = false
        }
        @Suppress("UnstableApiUsage")
        project.repositories {
            this.mavenCentral()
            this.add(this.maven("https://repo.spongepowered.org/repository/maven-public/") {
                name = "Sponge"
                content {
                    includeGroupAndSubgroups("org.spongepowered")
                }
            })
            this.add(this.maven("https://maven.blamejared.com/") {
                name = "BlameJared"
                content {
                    includeGroupAndSubgroups("com.blamejared")
                    includeGroupAndSubgroups("mezz.jei")
                    includeGroupAndSubgroups("com.faux")
                    includeGroupAndSubgroups("org.openzen")
                }
            })
            this.add(this.maven("https://maven.parchmentmc.org/") {
                name = "ParchmentMC"
                content {
                    includeGroupAndSubgroups("org.parchmentmc")
                }
            })
            this.add(this.maven("https://maven.shedaniel.me/") {
                name = "REI"
                content {
                    includeGroupAndSubgroups("me.shedaniel")
                    includeGroupAndSubgroups("dev.architectury")
                }
            })
            configureEach {
                if (this is MavenArtifactRepository) {
                    when (this.url.toString()) {
                        "https://maven.minecraftforge.net",
                        "https://libraries.minecraft.net",
                        "https://repo.maven.apache.org/maven2",
                        "https://maven.fabricmc.net" -> {
                            try {
                                content {
                                    excludeGroup("curse.maven")
                                    excludeGroup("mezz.jei")
                                    excludeGroupAndSubgroups("com.blamejared")
                                    excludeGroupAndSubgroups("com.faux")
                                    excludeGroupAndSubgroups("org.openzen")
                                    excludeGroupAndSubgroups("me.shedaniel")
                                    excludeGroup("dev.architectury")
                                    excludeGroupAndSubgroups("org.parchmentmc")
                                    excludeGroupAndSubgroups("org.spongepowered")
                                }
                                println("Adding exclusions to $url")
                            } catch (ignored: Exception) {
                            }
                        }
                    }
                }
            }
        }

    }

    private fun applyJavaPlugin(project: Project) {
        project.plugins.apply(JavaLibraryPlugin::class.java)

        with(project.extensions.getByType(JavaPluginExtension::class.java)) {
            toolchain.languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.majorVersion))
            withSourcesJar()
            withJavadocJar()
            sourceSets {
                named("main") {
                    resources {
                        srcDirs.add(project.file("src/generated/resources"))
                    }
                }
                create("gametest") {
                    resources {
                        srcDirs.add(project.file("src/gametest/resources"))
                    }
                    compileClasspath += sourceSets["main"].runtimeClasspath
                    runtimeClasspath += sourceSets["main"].runtimeClasspath
                }
            }
        }

        project.tasks {
            named<JavaCompile>("compileTestJava") {
                this.options.isFork = true
                this.options.compilerArgs.add("-XDenableSunApiLintControl")
            }
            named<JavaCompile>("compileJava") {
                this.options.compilerArgs.add("-Acrafttweaker.processor.document.output_directory=${project.rootProject.file(Properties.DOCS_OUTPUT_DIR)}")
                this.options.compilerArgs.add("-Acrafttweaker.processor.document.multi_source=true")
            }

            withType<JavaCompile>().matching { notNeoTask(it) }.configureEach {
                this.options.encoding = StandardCharsets.UTF_8.toString()
                this.options.release.set(Versions.MOD_JAVA.toInt())
                Dependencies.ZENCODE.forEach {
                    source(depJava(project, it).sourceSets.getByName("main").allSource)
                }

            }

            withType<Javadoc>().matching { notNeoTask(it) }.configureEach {
                options {
                    encoding = StandardCharsets.UTF_8.toString()
                    // Javadoc defines this specifically as StandardJavadocDocletOptions
                    // but only has a getter for MinimalJavadocOptions, but let's just make sure to be safe
                    if (this is StandardJavadocDocletOptions) {
                        this.tags("docParam", "docEvent", "docShortDescription", "docObtention")
                        this.addStringOption("Xdoclint:none", "-quiet")
                    }
                }
                Dependencies.ZENCODE.forEach {
                    source(depJava(project, it).sourceSets.getByName("main").allJava)
                }
            }

            named<Jar>("sourcesJar") {
                Dependencies.ZENCODE.forEach {
                    from(depJava(project, it).sourceSets.getByName("main").allSource)
                }
            }

            named("compileGametestJava", JavaCompile::class.java) {
                outputs.upToDateWhen { false }
                if (this.project.name.equals("neoforge")) {
                    Dependencies.ZENCODE.forEach {
                        source(depJava(project, it).sourceSets.getByName("main").allSource)
                    }
                    Dependencies.ZENCODE_TEST.forEach {
                        source(depJava(project, it).sourceSets.getByName("test").allSource)
                    }
                }
            }

            withType(ProcessResources::class.java).matching { notNeoTask(it) }.configureEach {
                dependsOn(":StdLibs:zipItUp")
                from(project.files(project.evaluationDependsOn(":StdLibs").tasks.getByName("zipItUp").outputs))

                val properties = mapOf(
                        "version" to project.version,
                        "MOD" to Versions.MOD,
                        "JAVA" to Versions.MOD_JAVA,
                        "MINECRAFT" to Versions.MINECRAFT,
                        "FABRIC_LOADER" to Versions.FABRIC_LOADER,
                        "FABRIC" to Versions.FABRIC,
                        "FORGE" to Versions.FORGE,
                        "FORGE_LOADER" to Versions.FORGE_LOADER,
                        "NEO_FORGE" to Versions.NEO_FORGE,
                        "NEO_FORGE_LOADER" to Versions.NEO_FORGE_LOADER,
                        "GROUP" to Properties.GROUP,
                        "NAME" to Properties.MOD_NAME,
                        "AUTHOR" to Properties.MOD_AUTHOR,
                        "MOD_ID" to Properties.MOD_ID,
                        "AVATAR" to Properties.MOD_AVATAR,
                        "CURSE_PROJECT_ID" to Properties.CURSE_PROJECT_ID,
                        "CURSE_HOMEPAGE_LINK" to Properties.CURSE_HOMEPAGE_LINK,
                        "MODRINTH_PROJECT_ID" to Properties.MODRINTH_PROJECT_ID,
                        "GIT_REPO" to Properties.GIT_REPO,
                        "DESCRIPTION" to Properties.DESCRIPTION,
                        "ITEM_ICON" to Properties.ITEM_ICON,
                )
                inputs.properties(properties)
                filesMatching(setOf("fabric.mod.json", "META-INF/mods.toml", "pack.mcmeta")) {
                    expand(properties)
                }
            }

            withType<Jar>().matching { notNeoTask(it) }.configureEach {
                manifest {
                    attributes["Specification-Title"] = Properties.MOD_NAME
                    attributes["Specification-Vendor"] = Properties.SIMPLE_AUTHOR
                    attributes["Specification-Version"] = archiveVersion
                    attributes["Implementation-Title"] = project.name
                    attributes["Implementation-Version"] = archiveVersion
                    attributes["Implementation-Vendor"] = Properties.SIMPLE_AUTHOR
                    attributes["Implementation-Timestamp"] = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
                    attributes["Timestamp"] = System.currentTimeMillis()
                    attributes["Built-On-Java"] = "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})"
                    attributes["Build-On-Minecraft"] = Versions.MINECRAFT
                }
            }
        }

    }


    private fun createConfigurations(project: Project) {
        this.gametestLibrary = project.configurations.create("gametestLibrary")
        this.library = project.configurations.create("library")
        this.localOnlyRuntime = project.configurations.create("localOnlyRuntime")
        project.configurations.getByName("gametestImplementation").extendsFrom(gametestLibrary)
        project.configurations.getByName("implementation").extendsFrom(library)

        project.configurations.getByName("runtimeClasspath").extendsFrom(localOnlyRuntime)
        project.configurations.getByName("gametestRuntimeClasspath").extendsFrom(gametestLibrary)
        // fabric loader 0.15 adds mixinextras which causes a crash due to us pulling in other ASM versions from ZC
        project.configurations.all {
            resolutionStrategy {
                force("org.ow2.asm:asm:9.6")
                force("org.ow2.asm:asm-commons:9.6")
            }
        }
    }

    private fun applyDependencies(project: Project) {
        val implementation = project.configurations.getByName("implementation")
        val gametestImplementation = project.configurations.getByName("gametestImplementation")

        project.configurations.getByName("annotationProcessor").dependencies.add(project.dependencies.create("com.blamejared.crafttweaker:Crafttweaker_Annotation_Processors:${Versions.CRAFTTWEAKER_ANNOTATION_PROCESSOR}"))

        gametestLibrary.dependencies.add(project.dependencies.create("org.hamcrest:hamcrest:${Versions.HAMCREST}"))
        gametestLibrary.dependencies.add(project.dependencies.create("org.junit.jupiter:junit-jupiter-engine:${Versions.JUPITER_ENGINE}"))
        gametestLibrary.dependencies.add(project.dependencies.create("org.junit.jupiter:junit-jupiter-params:${Versions.JUPITER_ENGINE}"))
        gametestLibrary.dependencies.add(project.dependencies.create("org.junit.platform:junit-platform-launcher:${Versions.JUNIT_PLATFORM_LAUNCHER}"))

        Dependencies.ZENCODE.forEach {
            val projectDep = project.dependencies.project(it)
            implementation.dependencies.add(projectDep)
            gametestImplementation.dependencies.add(projectDep)
        }

        Dependencies.ZENCODE_TEST.forEach {
            val depJava = depJava(project, it)
            gametestImplementation.dependencies.add(project.dependencies.create(depJava.sourceSets.getByName("test").output))
        }

    }


    private fun applyIdeaPlugin(project: Project) {
        project.plugins.apply(IdeaPlugin::class.java)

        val idea = project.extensions.getByType<IdeaModel>()
        idea.module.excludeDirs.addAll(setOf(project.file("run"), project.file("run_server"), project.file("run_client"), project.file("run_game_test")))
    }

    private fun applyMavenPlugin(project: Project) {
        project.plugins.apply(MavenPublishPlugin::class.java)

        val publishing = project.extensions.getByType<PublishingExtension>()
        val base = project.extensions.getByType<BasePluginExtension>()
        publishing.publications.register("mavenJava", MavenPublication::class.java) {
            artifactId = base.archivesName.get()
            from(project.components.getByName("java"))
        }
        publishing.repositories {
            maven("file:///${System.getenv("local_maven")}")
        }
    }

    private fun notNeoTask(task: Task): Boolean {
        return !task.name.startsWith("neo")
    }

    private fun common(project: Project): Project {
        return project.project(":common")
    }

    private fun depJava(project: Project, other: String): JavaPluginExtension {
        return project.project(other).extensions.getByType(JavaPluginExtension::class.java)
    }
}
