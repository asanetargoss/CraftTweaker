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

package crafttweaker.mc1102.brewing;

import java.util.List;
import java.util.stream.Collectors;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class MultiBrewingRecipe implements IBrewingRecipe {
	
	private final IIngredient input, ingredient;
	private final ItemStack output;
	private final boolean visible;

	public MultiBrewingRecipe(IIngredient input, IIngredient[] ingredients, IItemStack output, boolean visible) {
		this.input = input;
		this.output = MineTweakerMC.getItemStack(output);
		this.visible = visible;
		this.ingredient = readIngredientArray(ingredients);
	}

	@Override
	public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		return (isInput(input) && isIngredient(ingredient)) ? getOutput() : null;
	}

	@Override
	public boolean isIngredient(ItemStack tester) {
		return ingredient.matches(MineTweakerMC.getIItemStack(tester)); 
	}

	@Override
	public boolean isInput(ItemStack tester) {
		return input.matches(MineTweakerMC.getIItemStack(tester));
	}
	
	public ItemStack getOutput() {
		return output.copy();
	}
	
	public List<ItemStack> getInputs() {
		return input.getItems().stream().map(MineTweakerMC::getItemStack).collect(Collectors.toList());
	}
	
	public List<ItemStack> getIngredients() {
		return ingredient.getItems().stream().map(MineTweakerMC::getItemStack).collect(Collectors.toList());
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public boolean isValid() {
		return !input.getItems().isEmpty() && !ingredient.getItems().isEmpty() && output != null;
	}

	/**
	 * Condenses all ingredients into one using the or method
	 * @return ingredients as single ingredient object
	 */
	public IIngredient readIngredientArray(IIngredient[] ingredients) {
		//Shouldn't happen
		if(ingredients.length == 0) {
			throw new IllegalArgumentException("Brewing ingredient list may not be empty");
		//Why should we or if there's only one ingredient?
		} else if (ingredients.length == 1) {
			return ingredients[0];
		//or's all IIngredients to get one resulting ingredient
		} else {
			IIngredient ing = ingredients[0];
			for (int i = 1; i < ingredients.length; i++) {
				ing = ing.or(ingredients[i]);
			}
			return ing;
		}
	}
}
