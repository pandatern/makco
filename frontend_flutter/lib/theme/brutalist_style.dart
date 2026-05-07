import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppleColors {
  static const Color white = Color(0xFFFFFFFF);
  static const Color black = Color(0xFF000000);
  static const Color bg = Color(0xFFFFFFFF); // Pure White Background
  static const Color surface = Color(0xFFFFFFFF);
  static const Color blue = Color(0xFF000000); // Standard actions are now Pure Black
  static const Color gray = Color(0xFF000000); // High contrast
  static const Color lightGray = Color(0xFFEEEEEE);
  static const Color error = Color(0xFF000000); // Pure B&W
}

class AppleStyle {
  static const double borderRadius = 12.0; // Slightly tighter corners for B&W look
  static const double squircleRadius = 16.0;

  static BoxDecoration cardDecoration({
    Color color = AppleColors.surface,
    double radius = borderRadius,
    bool hasShadow = false, // Pure B&W often looks better with borders than shadows
  }) {
    return BoxDecoration(
      color: color,
      borderRadius: BorderRadius.circular(radius),
      border: Border.all(color: AppleColors.black, width: 1.5), // Sharp black border
      boxShadow: hasShadow
          ? [
              const BoxShadow(
                color: Colors.black,
                offset: Offset(4, 4), // Hard offset shadow for depth
                blurRadius: 0,
              )
            ]
          : [],
    );
  }

  static TextStyle largeTitle() {
    return GoogleFonts.inter(
      fontSize: 34,
      fontWeight: FontWeight.w900, // Extra bold for B&W
      color: AppleColors.black,
      letterSpacing: -1.0,
    );
  }

  static TextStyle title() {
    return GoogleFonts.inter(
      fontSize: 20,
      fontWeight: FontWeight.w800,
      color: AppleColors.black,
    );
  }

  static TextStyle body({bool bold = false, Color color = AppleColors.black}) {
    return GoogleFonts.inter(
      fontSize: 17,
      fontWeight: bold ? FontWeight.w700 : FontWeight.w500,
      color: color,
    );
  }

  static TextStyle footnote() {
    return GoogleFonts.inter(
      fontSize: 13,
      fontWeight: FontWeight.w600,
      color: AppleColors.black,
    );
  }
}
