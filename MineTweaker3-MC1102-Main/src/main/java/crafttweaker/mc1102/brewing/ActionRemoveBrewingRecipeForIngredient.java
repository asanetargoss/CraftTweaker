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

import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.List;

/**
 * @author youyihj
 */
public class ActionRemoveBrewingRecipeForIngredient implements IBrewingAction {
    private final IItemStack ingredient;
    private final List<IBrewingRecipe> allBrewingRecipes;

    public ActionRemoveBrewingRecipeForIngredient(IItemStack ingredient, List<IBrewingRecipe> allBrewingRecipes) {
        this.ingredient = ingredient;
        this.allBrewingRecipes = allBrewingRecipes;
    }

    @Override
    public void apply() {
        ItemStack _ingredient = MineTweakerMC.getItemStack(ingredient);
        allBrewingRecipes.removeIf(it -> it.getClass() != VanillaBrewingPlus.class && it.isIngredient(_ingredient));
    }

    @Override
    public String describe() {
        return "Removing brewing recipes for ingredient: " + ingredient;
    }
}
