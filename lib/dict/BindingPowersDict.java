package lib.dict;

import java.util.function.Function;

import ds.BindingPower;
import ds.Dict;
import lib.expression.*;

public class BindingPowersDict extends Dict<BindingPower> {

    public BindingPowersDict() {
        super(
            new BindingPower(100, false),
            new BindingPower(100, false),
            new BindingPower(10, false),
            new BindingPower(10, false),
            new BindingPower(20, false),
            new BindingPower(20, false),
            new BindingPower(30, true),
            new BindingPower(20, false),
            new BindingPower(40, true),
            new BindingPower(5, false),
            new BindingPower(5, false),
            new BindingPower(5, false),
            new BindingPower(5, false),
            new BindingPower(5, false),
            new BindingPower(5, false),
            new BindingPower(3, false),
            new BindingPower(2, false),
            new BindingPower(30, true),
            new BindingPower(1, true),
            new BindingPower(50, false)
        );
    }
}