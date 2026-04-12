package lib.dict;

import ds.BindingPower;
import ds.Dict2;

public class BindingPowersDict2 extends Dict2<BindingPower> {
    public BindingPowersDict2() {
        super(new BindingPowersDict(), new BindingPower(30, true));
    }
}
