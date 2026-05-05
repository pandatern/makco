import 'package:flutter/material.dart';

// Replacing vibrant brutalist with Kotlin's pure monochrome neo-brutalist theme
class BrutalistColors {
  static const Color white = Color(0xFFFFFFFF);
  static const Color black = Color(0xFF000000);
  
  static const Color gray = Color(0xFFFFFFFF); // Backgrounds are usually white
  static const Color darkGray = Color(0xFF333333);
  
  static const Color primary = Color(0xFF000000); // Action is black in light theme
  static const Color accent = Color(0xFF000000); // Action is black
  static const Color error = Color(0xFF000000); // Kotlin theme had error = t1 (which is black)
}

class BrutalistStyle {
  static const double borderWeight = 2.0; // Kotlin used 2.dp or 3.dp
  static const double borderRadius = 16.0;

  static BoxDecoration containerDecoration({
    Color color = BrutalistColors.white,
    double radius = borderRadius,
    bool hasShadow = true,
  }) {
    return BoxDecoration(
      color: color,
      border: Border.all(color: color == BrutalistColors.black ? BrutalistColors.black : const Color(0xFFCCCCCC), width: borderWeight),
      borderRadius: BorderRadius.circular(radius),
      boxShadow: hasShadow
          ? [
              BoxShadow(
                color: Colors.black.withOpacity(0.15),
                offset: const Offset(0, 4), // Soft elevation shadow, not hard offset
                blurRadius: 8,
              )
            ]
          : [],
    );
  }

  // Pure monochrome SansSerif (Roboto is default SansSerif in Flutter)
  static TextStyle heading({Color color = BrutalistColors.black}) {
    return TextStyle(
      fontFamily: 'Roboto',
      fontSize: 32,
      fontWeight: FontWeight.w900,
      color: color,
      letterSpacing: 1,
    );
  }

  static TextStyle title({Color color = BrutalistColors.black}) {
    return TextStyle(
      fontFamily: 'Roboto',
      fontSize: 22,
      fontWeight: FontWeight.bold,
      color: color,
    );
  }

  static TextStyle body({Color color = BrutalistColors.black, bool bold = false}) {
    return TextStyle(
      fontFamily: 'Roboto',
      fontSize: 16,
      fontWeight: bold ? FontWeight.bold : FontWeight.normal,
      color: color,
    );
  }

  static TextStyle label({Color color = BrutalistColors.black}) {
    return TextStyle(
      fontFamily: 'Roboto',
      fontSize: 14,
      fontWeight: FontWeight.bold,
      color: color,
      letterSpacing: 1,
    );
  }
}
