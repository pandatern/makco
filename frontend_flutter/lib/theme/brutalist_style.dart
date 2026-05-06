import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class BrutalistColors {
  static const Color white = Color(0xFFFFFFFF);
  static const Color black = Color(0xFF000000);
  static const Color bg = Color(0xFFF0F0F0);
  static const Color primary = Color(0xFF000000);
  static const Color accent = Color(0xFFFFD700); // Gold accent for "Premium" touch
  static const Color error = Color(0xFFFF4545);
}

class BrutalistStyle {
  static const double borderWeight = 3.5;
  static const double borderRadius = 0.0; // Sharp corners for hardcore brutalism
  static const Offset shadowOffset = Offset(8, 8);

  static BoxDecoration box({
    Color color = BrutalistColors.white,
    bool hasShadow = true,
    Color shadowColor = BrutalistColors.black,
  }) {
    return BoxDecoration(
      color: color,
      border: Border.all(color: BrutalistColors.black, width: borderWeight),
      boxShadow: hasShadow
          ? [
              BoxShadow(
                color: shadowColor,
                offset: shadowOffset,
                blurRadius: 0,
              )
            ]
          : [],
    );
  }

  static TextStyle heading() {
    return GoogleFonts.archivoBlack(
      fontSize: 42,
      color: BrutalistColors.black,
      height: 1.1,
    );
  }

  static TextStyle title() {
    return GoogleFonts.spaceGrotesk(
      fontSize: 24,
      fontWeight: FontWeight.w900,
      color: BrutalistColors.black,
      letterSpacing: -0.5,
    );
  }

  static TextStyle body({bool bold = false}) {
    return GoogleFonts.spaceGrotesk(
      fontSize: 16,
      fontWeight: bold ? FontWeight.w800 : FontWeight.w500,
      color: BrutalistColors.black,
    );
  }

  static TextStyle label() {
    return GoogleFonts.spaceGrotesk(
      fontSize: 13,
      fontWeight: FontWeight.w900,
      color: BrutalistColors.black,
      letterSpacing: 2,
    );
  }
}
