package com.blamejared.crafttweaker.api.recipe.function;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import org.openzen.zencode.java.ZenCodeType;

@FunctionalInterface
@ZenRegister
@ZenCodeType.Name("crafttweaker.api.recipe.function.RecipeFunctionArray")
@Document("vanilla/api/recipe/function/RecipeFunctionArray")
public interface RecipeFunctionArray {
    
    @ZenCodeType.Method
    IItemStack process(IItemStack usualOut, IItemStack[] inputs);
    
}