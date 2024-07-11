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

package crafttweaker.mc1102;

import java.util.List;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import net.minecraftforge.fml.common.ProgressManager;

public class CraftTweaker {
    public static void applyActions(List<? extends IUndoableAction> actions, String applyingMessage, String errorMessage) {
        if (!actions.isEmpty()) {
            ProgressManager.ProgressBar progressBar = ProgressManager.push(applyingMessage, actions.size());
            actions.forEach(action -> {
                progressBar.step(action.describe());
                try {
                    MineTweakerAPI.apply(action);
                } catch (Exception e) {
                    MineTweakerImplementationAPI.logger.logError(errorMessage + " at action " + action.describe(), e);
                }
            });
            ProgressManager.pop(progressBar);
        }
    }

}
