package com.blamejared.crafttweaker.api.util;


import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

/**
 * Set of utility methods related to names and naming in general.
 */
@Document("vanilla/api/util/NameUtil")
@ZenCodeType.Name("crafttweaker.util.NameUtil")
@ZenRegister
public final class NameUtil {
    
    private static final String AUTOGENERATED_MARKER = "autogenerated/";
    
    /**
     * Creates a {@link ResourceLocation} from the given {@code input}, if possible, while fixing mistakes that
     * may be present in the string.
     *
     * @param input The string that should be fixed and converted to a {@link ResourceLocation}.
     *
     * @return A {@link ResourceLocation} that represents the fixed input.
     *
     * @throws IllegalArgumentException  If the string cannot be automatically fixed.
     * @throws ResourceLocationException If the string cannot be automatically fixed.
     */
    @ZenCodeType.Method
    public static ResourceLocation fromFixedName(final String input) {
        
        return fromFixedName(input, (fix, mistakes) -> {});
    }
    
    /**
     * Creates a {@link ResourceLocation} from the given {@code input}, if possible, while fixing mistakes that
     * may be present in the string.
     *
     * @param input          The string that should be fixed and converted to a {@link ResourceLocation}.
     * @param mistakeHandler A bi-consumer that gets called if there were any mistakes in the original string. The first
     *                       element is the fixed string, and the second is a list of strings containing explanations
     *                       for all the identified mistakes.
     *
     * @return A {@link ResourceLocation} that represents the fixed input.
     *
     * @throws IllegalArgumentException  If the string cannot be automatically fixed.
     * @throws ResourceLocationException If the string cannot be automatically fixed.
     */
    @ZenCodeType.Method
    public static ResourceLocation fromFixedName(final String input, final BiConsumer<String, List<String>> mistakeHandler) {
        
        return CraftTweakerConstants.rl(fixing(input, mistakeHandler));
    }
    
    /**
     * Attempts to automatically fix the given {@code input} string, if possible, so that it can be used to build a
     * well-formed {@link ResourceLocation}.
     *
     * @param input The string that should be fixed to a {@link ResourceLocation}-compatible format.
     *
     * @return The fixed string.
     *
     * @throws IllegalArgumentException  If the string cannot be automatically fixed.
     * @throws ResourceLocationException If the string cannot be automatically fixed.
     */
    @ZenCodeType.Method
    public static String fixing(final String input) {
        
        return fixing(input, (fix, mistakes) -> {});
    }
    
    /**
     * Attempts to automatically fix the given {@code input} string, if possible, so that it can be used to build a
     * well-formed {@link ResourceLocation}.
     *
     * @param input          The string that should be fixed to a {@link ResourceLocation}-compatible format.
     * @param mistakeHandler A bi-consumer that gets called if there were any mistakes in the original string. The first
     *                       element is the fixed string, and the second is a list of strings containing explanations
     *                       for all the identified mistakes.
     *
     * @return The fixed string.
     *
     * @throws IllegalArgumentException  If the string cannot be automatically fixed.
     * @throws ResourceLocationException If the string cannot be automatically fixed.
     */
    @ZenCodeType.Method
    public static String fixing(final String input, final BiConsumer<String, List<String>> mistakeHandler) {
        
        final List<String> mistakes = new ArrayList<>();
        /*mutable*/
        String fixed = input;
        
        if(fixed.indexOf(':') >= 0) {
            mistakes.add("- it cannot contain colons (':')");
            fixed = fixed.replace(':', '.');
        }
        if(fixed.indexOf(' ') >= 0) {
            mistakes.add("- it cannot contain spaces");
            fixed = fixed.replace(' ', '.');
        }
        if(!fixed.toLowerCase(Locale.ENGLISH).equals(fixed)) {
            mistakes.add("- it must be all lowercase");
            fixed = fixed.toLowerCase(Locale.ENGLISH);
        }
        if(fixed.startsWith(AUTOGENERATED_MARKER)) {
            mistakes.add("- it cannot reside in the reserved 'autogenerated' subdomain");
            fixed = fixed.substring(AUTOGENERATED_MARKER.length());
        }
        
        try {
            // Specifically not using CraftTweakerConstants.MOD_ID as that may change
            new ResourceLocation(CraftTweakerConstants.MOD_ID, fixed); // Initializing it for side effects
            if(!mistakes.isEmpty()) {
                mistakeHandler.accept(fixed, mistakes);
            }
            return fixed;
        } catch(final ResourceLocationException | IllegalArgumentException e) {
            mistakes.add("- " + e.getMessage());
            mistakeHandler.accept("<unable to auto-fix, best attempt: " + fixed + '>', mistakes);
            throw e;
        }
    }
    
    /**
     * Verifies whether the given name has been autogenerated by CraftTweaker.
     *
     * @param name The name to verify.
     *
     * @return Whether the name has been autogenerated by CraftTweaker.
     */
    @ZenCodeType.Method
    public static boolean isAutogeneratedName(final ResourceLocation name) {
        
        return CraftTweakerConstants.MOD_ID.equals(name.getNamespace()) && name.getPath()
                .startsWith(AUTOGENERATED_MARKER);
    }
    
    // Not exposing to ZenCode since we don't want people to be able to autogenerate stuff without us noticing
    public static ResourceLocation generateNameFrom(final String discriminator, final String name) {
        
        return CraftTweakerConstants.rl(AUTOGENERATED_MARKER + discriminator + '/' + name);
    }
    
}
