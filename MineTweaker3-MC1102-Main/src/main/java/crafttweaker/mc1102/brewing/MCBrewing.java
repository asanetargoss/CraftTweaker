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
import minetweaker.api.item.IngredientAny;
import crafttweaker.api.recipes.IBrewingManager;
import minetweaker.mc1102.util.MineTweakerHacks;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.ArrayList;
import java.util.List;

// TODO: JEI functionality
public class MCBrewing implements IBrewingManager{
    private static final List<Tuple<IIngredient, IIngredient>> toRemoveVanillaRecipes = new ArrayList<>();
    private static final List<IBrewingRecipe> allBrewingRecipes = MineTweakerHacks.getPrivateStaticObject(BrewingRecipeRegistry.class, "recipes");
    // TODO: This could be useful for undo
    //public static final List<IBrewingAction> brewingActions = new ArrayList<>();

    public MCBrewing() {
    }

    @Override
    public void addBrew(IIngredient input, IIngredient ingredient, IItemStack output, boolean hidden) {
        //brewingActions.add(new ActionAddBrewingRecipe(input, new IIngredient[] {ingredient}, output, hidden));
        MineTweakerAPI.apply(new ActionAddBrewingRecipe(input, new IIngredient[] {ingredient}, output, hidden));        
    }

    @Override
    public void addBrew(IIngredient input, IIngredient[] ingredients, IItemStack output, boolean hidden) {
        //brewingActions.add(new ActionAddBrewingRecipe(input, ingredients, output, hidden));
        MineTweakerAPI.apply(new ActionAddBrewingRecipe(input, ingredients, output, hidden));
    }

    @Override
    public void removeRecipe(IItemStack input, IItemStack ingredient) {
        addFixVanillaRecipesAction();
        // TODO: Appending to toRemoveVanillaRecipes should be part of the IBrewingAction
        toRemoveVanillaRecipes.add(new Tuple<>(input, ingredient));
        //brewingActions.add(new ActionRemoveBrewingRecipe(input, ingredient, allBrewingRecipes));
        MineTweakerAPI.apply(new ActionRemoveBrewingRecipe(input, ingredient, allBrewingRecipes));
    }

    @Override
    public void removeRecipe(IItemStack ingredient) {
        addFixVanillaRecipesAction();
        // TODO: Appending to toRemoveVanillaRecipes should be part of the IBrewingAction
        toRemoveVanillaRecipes.add(new Tuple<>(IngredientAny.INSTANCE, ingredient));
        //brewingActions.add(new ActionRemoveBrewingRecipeForIngredient(ingredient, allBrewingRecipes));
        MineTweakerAPI.apply(new ActionRemoveBrewingRecipeForIngredient(ingredient, allBrewingRecipes));
    }

    private static void addFixVanillaRecipesAction() {
        // TODO: Does this actually make sense? There might be a reason the actions are added to lists...
        if (toRemoveVanillaRecipes.isEmpty()) { // toRemoveVanillaRecipes list is empty means the first time to call the method
            ActionFixVanillaBrewingRecipes fix = new ActionFixVanillaBrewingRecipes(toRemoveVanillaRecipes, allBrewingRecipes);
            //brewingActions.add(fix);
            MineTweakerAPI.apply(fix);
        }
    }
}
