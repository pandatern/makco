import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppleColors {
  static const Color white = Color(0xFFFFFFFF);
  static const Color black = Color(0xFF000000);
  static const Color bg = Color(0xFFF2F2F7); // iOS System Background
  static const Color surface = Color(0xFFFFFFFF);
  static const Color blue = Color(0xFF007AFF); // iOS Blue
  static const Color gray = Color(0xFF8E8E93); // iOS Gray
  static const Color lightGray = Color(0xFFD1D1D6);
  static const Color error = Color(0xFFFF3B30); // iOS Red
}

class AppleStyle {
  static const double borderRadius = 20.0;
  static const double squircleRadius = 24.0;

  static BoxDecoration cardDecoration({
    Color color = AppleColors.surface,
    double radius = borderRadius,
    bool hasShadow = true,
  }) {
    return BoxDecoration(
      color: color,
      borderRadius: BorderRadius.circular(radius),
      boxShadow: hasShadow
          ? [
              BoxShadow(
                color: Colors.black.withOpacity(0.05),
                offset: const Offset(0, 4),
                blurRadius: 12,
              )
            ]
          : [],
    );
  }

  static TextStyle largeTitle() {
    return GoogleFonts.inter(
      fontSize: 34,
      fontWeight: FontWeight.w700,
      color: AppleColors.black,
      letterSpacing: -0.5,
    );
  }

  static TextStyle title() {
    return GoogleFonts.inter(
      fontSize: 20,
      fontWeight: FontWeight.w600,
      color: AppleColors.black,
      letterSpacing: -0.3,
    );
  }

  static TextStyle body({bool bold = false, Color color = AppleColors.black}) {
    return GoogleFonts.inter(
      fontSize: 17,
      fontWeight: bold ? FontWeight.w600 : FontWeight.w400,
      color: color,
    );
  }

  static TextStyle footnote() {
    return GoogleFonts.inter(
      fontSize: 13,
      fontWeight: FontWeight.w400,
      color: AppleColors.gray,
    );
  }
}
