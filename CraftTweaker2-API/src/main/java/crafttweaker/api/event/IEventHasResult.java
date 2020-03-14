package crafttweaker.api.event;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenSetter;

@ZenClass("crafttweaker.event.IEventHasResult")
@ZenRegister
public interface IEventHasResult {
    @ZenMethod
    @ZenGetter("isDenied")
    boolean isDenied();

    @ZenMethod
    @ZenGetter("isDefault")
    boolean isDefault();

    @ZenMethod
    @ZenGetter("isAllowed")
    boolean isAllowed();

    @ZenMethod
    @ZenGetter("deny")
    void setDenied ();

    @ZenMethod
    @ZenGetter("default")
    void setDefault ();

    @ZenMethod
    @ZenGetter("allow")
    void setAllowed ();
}
