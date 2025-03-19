package reactor;

import java.awt.Color;

public record UnitTemplate(String type, String subtype, Color color, KeyValueList temp, KeyValueList global) {}
