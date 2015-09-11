package io.github.xxyy.mtc.module.showhomes;

import java.text.DecimalFormat;
import java.util.Locale;

public final class ShowHomesConstants { //TODO remove

    static final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) DecimalFormat.getNumberInstance(Locale.GERMAN);

    static final int DEFAULT_RADIUS_DEFAULT = 20;
    static final int MAX_RADIUS_DEFAULT = 50;
    static final int HOLOGRAM_DURATION_DEFAULT = 60;
    static final int HOLOGRAM_RATE_LIMIT_DEFAULT = 20;

    static {
        ShowHomesConstants.DECIMAL_FORMAT.applyPattern("###.##");
    }

    private ShowHomesConstants() {
        throw new UnsupportedOperationException();
    }
}
