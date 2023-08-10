package org.heigit.ors.snapping;

public class SnappingErrorCodes {
    public static final int BASE = 8000;
    public static final int INVALID_JSON_FORMAT = 8000;
    public static final int MISSING_PARAMETER = 8001;
    public static final int INVALID_PARAMETER_FORMAT = 8002;
    public static final int INVALID_PARAMETER_VALUE = 8003;
    public static final int UNKNOWN_PARAMETER = 8004;
    public static final int MISMATCHED_INPUT = 8005;
    public static final int UNSUPPORTED_EXPORT_FORMAT = 8006;
    public static final int UNKNOWN = 8099;

    private SnappingErrorCodes() {
    }

}
