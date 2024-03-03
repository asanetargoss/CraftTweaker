# MineTweaker 3 CUSTOMIZED

This is a fork of Minetweaker 3 (legacy branch) for Minecraft 1.10, also known as CraftTweaker, created mainly for the purpose of improving the Hardcore Alchemy modpack, but which may be used in other 1.10 modpacks if desired.

For 1.7.10, see the GTNH fork. For 1.11 and above, use Minetweaker upstream (aka CraftTweaker).

## License

Credit for the original Minetweaker 3 goes to Stan Hebben and other contributors.

Copied from the upstream readme, the licensing info is as follows:

```
The source code is freely available in here, at GitHub. The mod itself may be redistributed in modpacks as long as these modpacks are distributed for free. No money can be made from the distribution of the mod.

You may modify and redistribute your own customized version of MineTweaker as long as you mark your own version of MineTweaker as CUSTOMIZED (that is, put CUSTOMIZED in both the jar and mod name) and as long as you make the modified source code publicly available. Obviously, I cannot offer support for customized builds. Just be so kind to mention the original source and author (me) ;)
```

Please note that this license only applies to this version of the Minetweaker codebase. Newer versions of the Minetweaker source code may be under a more permissive license, and may be referred to as, "CraftTweaker."

## Building

A few important notes:

- Projects of other Minecraft versions besides 1.10 are definitely broken right now.
- Build instructions are WIP

Use [jenv](https://github.com/jenv/jenv/) or equivalent tool to set the gradlew java version to Java 8.

These gradle wrapper commands (`./gradlew [command]`) are probably important. They may or may not work:

- `setupDecompWorkspaceAll`
- `MineTweaker3-API:eclipse` 
- `MineTweaker3-MC1102-Main:setupDecompWorkspace`
- `MineTweaker3-MC1102-Main:eclipse`
- `assembleAll`

Note: If buildSrc fails to compile, you might have to edit the +`javaToolsJar` location in `configuration.gradle`

`setupDecompWorkspace` commands set up Minecraft, Forge, etc. `eclipse` can then create Eclipse projects that can be imported into a workspace. There might be other important commands forgotten at the time of writing, but typically the part before the colon is a directory name. See also `./gradlew tasks`.

The top-level directory of this git repository, or any higher directory, is a suitable place for an Eclipse workspace.

After running the necessary eclipse commands, import these projects into your workspace:

- ZenScript
- Minetweaker3-API
- Minetweaker3-MC1102-Main
- Minetweaker3-MC1102-Mod-IC2
- Minetweaker3-MC1102-Mod-JEI

Now the projects are imported into Eclipse, but most projects are erroring due to Eclipse using Java 11, when it should be using Java 8. Can theoretically be fixed in the Eclipse project, but ideally would be fixed with gradle.

TODO: Finish documenting build steps
