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

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class ActionAddBrewingRecipe implements IBrewingAction {
    private final MultiBrewingRecipe recipe;
    private final String outName;
    private final boolean valid;

    public ActionAddBrewingRecipe(IIngredient input, IIngredient[] ingredients, IItemStack output, boolean hidden) {
        this.outName = output.toString();
        this.recipe = new MultiBrewingRecipe(input, ingredients, output, !hidden);
        this.valid = recipe.isValid();
    }


    @Override
    public void apply() {
        if (!valid) {
            MineTweakerAPI.logError(String.format("Brewing recipe for %s is invalid", outName));
            return;
        }
        BrewingRecipeRegistry.addRecipe(recipe);
    }

    @Override
    public String describe() {
        return "Adding brewing recipe for " + outName + ", Registry size now: " + BrewingRecipeRegistry.getRecipes().size();
    }
}
