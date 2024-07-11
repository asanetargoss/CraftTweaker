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

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.item.IngredientAny;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;

import javax.annotation.Nonnull;
import java.util.List;

public final class VanillaBrewingPlus extends VanillaBrewingRecipe {

    private final List<Tuple<IIngredient, IIngredient>> removedRecipes;

    public VanillaBrewingPlus(List<Tuple<IIngredient, IIngredient>> removedRecipes) {
        this.removedRecipes = removedRecipes;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        IItemStack _input = MineTweakerMC.getIItemStack(input);
        IItemStack _ingredient = MineTweakerMC.getIItemStack(ingredient);

        if (removedRecipes.stream().anyMatch(t -> t.getFirst().matches(_input) && t.getSecond().matches(_ingredient))) {
            return null;
        }

        return super.getOutput(input, ingredient);
    }

    @Override
    public boolean isIngredient(@Nonnull ItemStack stack) {
        IItemStack _ingredient = MineTweakerMC.getIItemStack(stack);

        return super.isIngredient(stack) && removedRecipes.stream().noneMatch(t -> t.getFirst() == IngredientAny.INSTANCE && t.getSecond().matches(_ingredient));
    }

    public ItemStack getRealOutput(ItemStack input, ItemStack ingredient) {
        return super.getOutput(input, ingredient);
    }

    public List<Tuple<IIngredient, IIngredient>> getRemovedRecipes() {
        return removedRecipes;
    }
}
