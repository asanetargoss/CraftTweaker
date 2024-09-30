/**
 * MIT License
 *
 * Copyright (c) 2019 CraftTweaker
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package crafttweaker.mods.jei.recipeWrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import crafttweaker.mc1102.brewing.MultiBrewingRecipe;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.plugins.vanilla.brewing.BrewingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class BrewingRecipeCWrapper extends BrewingRecipeWrapper  {
	
	public final ItemStack output;
	public final List<List<ItemStack>> ingredientList;
	public final List<ItemStack> inputs;
	
	public BrewingRecipeCWrapper(MultiBrewingRecipe recipe) {
	    // -1 = don't know brewing step count
		super(recipe.getIngredients(), recipe.getInputs().get(0), recipe.getOutput(), -1);
		
		this.inputs = recipe.getInputs();
		this.ingredientList = Arrays.asList(recipe.getInputs(), recipe.getInputs(), recipe.getInputs(), recipe.getIngredients());
		this.output = recipe.getOutput();
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
	    ingredients.setInputLists(ItemStack.class, ingredientList);
		ingredients.setOutput(ItemStack.class, output);
	}
	
	@Override
	public List<?> getInputs() {
		// returns our inputs
	    // This function behaves differently in 1.12
		//return inputs;
	    return ingredientList;
	}
	
	//Static Methods//	
    public static List<IRecipeWrapper> createBrewingRecipes() {
        return BrewingRecipeRegistry.getRecipes()
                        .stream()
                        .filter(MultiBrewingRecipe.class::isInstance)
                        .map(MultiBrewingRecipe.class::cast)
                        .filter(MultiBrewingRecipe::isVisible)
                        .map(BrewingRecipeCWrapper::new)
                        .collect(Collectors.toList());
    }
}