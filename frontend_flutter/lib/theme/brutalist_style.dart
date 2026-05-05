import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class BrutalistColors {
  static const Color white = Color(0xFFFFFFFF);
  static const Color black = Color(0xFF000000);
  static const Color gray = Color(0xFFF4F4F4);
  static const Color darkGray = Color(0xFF404040);
  static const Color primary = Color(0xFF0055AA); // CMRL Blue
  static const Color accent = Color(0xFFB0D91E); // Lime Green for contrast
  static const Color error = Color(0xFFFF4040);
}

class BrutalistStyle {
  static const double borderWeight = 3.0;
  static const double borderRadius = 16.0;
  static const Offset shadowOffset = Offset(6, 6);

  static BoxDecoration containerDecoration({
    Color color = BrutalistColors.white,
    double radius = borderRadius,
    bool hasShadow = true,
  }) {
    return BoxDecoration(
      color: color,
      border: Border.all(color: BrutalistColors.black, width: borderWeight),
      borderRadius: BorderRadius.circular(radius),
      boxShadow: hasShadow
          ? [
              const BoxShadow(
                color: BrutalistColors.black,
                offset: shadowOffset,
                blurRadius: 0,
              )
            ]
          : [],
    );
  }

  static TextStyle heading({Color color = BrutalistColors.black}) {
    return GoogleFonts.spaceGrotesk(
      fontSize: 32,
      fontWeight: FontWeight.w900,
      color: color,
      letterSpacing: -1,
    );
  }

  static TextStyle title({Color color = BrutalistColors.black}) {
    return GoogleFonts.spaceGrotesk(
      fontSize: 22,
      fontWeight: FontWeight.w800,
      color: color,
    );
  }

  static TextStyle body({Color color = BrutalistColors.black, bool bold = false}) {
    return GoogleFonts.inter(
      fontSize: 16,
      fontWeight: bold ? FontWeight.w700 : FontWeight.w500,
      color: color,
    );
  }

  static TextStyle label({Color color = BrutalistColors.black}) {
    return GoogleFonts.spaceGrotesk(
      fontSize: 12,
      fontWeight: FontWeight.w700,
      color: color,
      letterSpacing: 1,
    );
  }
}
