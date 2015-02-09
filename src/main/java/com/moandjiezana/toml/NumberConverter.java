package com.moandjiezana.toml;

import static com.moandjiezana.toml.ValueConverterUtils.INVALID;

class NumberConverter implements ValueConverter {
  static final NumberConverter NUMBER_PARSER = new NumberConverter();
  
  @Override
  public boolean canConvert(String s) {
    char firstChar = s.charAt(0);
    
    return firstChar == '+' || firstChar == '-' || Character.isDigit(firstChar);
  }

  @Override
  public Object convert(String s) {
    char[] chars = s.toCharArray();
    boolean whitespace = false;
    boolean signable = true;
    boolean dottable = false;
    boolean exponentable = false;
    boolean underscorable = false;
    String type = "";
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < chars.length; i++) {
      char c = chars[i];
      boolean notLastChar = chars.length > i + 1;

      if (Character.isDigit(c)) {
        sb.append(c);
        signable = false;
        if (type.isEmpty()) {
          type = "integer";
          dottable = true;
        }
        underscorable = notLastChar;
        exponentable = !type.equals("exponent");
      } else if ((c == '+' || c == '-') && signable && notLastChar) {
        signable = false;
        if (c == '-') {
          sb.append('-');
        }
        underscorable = false;
      } else if (c == '.' && dottable && notLastChar) {
        sb.append('.');
        type = "float";
        dottable = false;
        exponentable = false;
        underscorable = false;
      } else if ((c == 'E' || c == 'e') && exponentable && notLastChar) {
        sb.append('E');
        type = "exponent";
        signable = true;
        dottable = false;
        exponentable = false;
        underscorable = false;
      } else if (c == '_' && underscorable && notLastChar && Character.isDigit(chars[i + 1])) {
        underscorable = false;
      } else if (Character.isWhitespace(c)) {
        whitespace = true;
      } else if (whitespace && c == '#') {
        break;
      } else {
        type = "";
        break;
      }
    }

    if (type.equals("integer")) {
      return Long.valueOf(sb.toString());
    } else if (type.equals("float")) {
      return Double.valueOf(sb.toString());
    } else if (type.equals("exponent")) {
      String[] exponentString = sb.toString().split("E");
      
      return Double.parseDouble(exponentString[0]) * Math.pow(10, Double.parseDouble(exponentString[1]));
    } else {
      return INVALID;
    }
  }
}
