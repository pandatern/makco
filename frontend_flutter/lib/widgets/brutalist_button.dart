import 'package:flutter/material.dart';
import '../theme/brutalist_style.dart';

class BrutalistButton extends StatefulWidget {
  final String text;
  final VoidCallback? onTap;
  final Color color;
  final bool isLoading;
  final double? width;

  const BrutalistButton({
    Key? key,
    required this.text,
    this.onTap,
    this.color = BrutalistColors.primary,
    this.isLoading = false,
    this.width,
  }) : super(key: key);

  @override
  _BrutalistButtonState createState() => _BrutalistButtonState();
}

class _BrutalistButtonState extends State<BrutalistButton> {
  bool _isPressed = false;

  @override
  Widget build(BuildContext context) {
    final bool isEnabled = widget.onTap != null && !widget.isLoading;
    
    return GestureDetector(
      onTapDown: (_) => setState(() => _isPressed = true),
      onTapUp: (_) => setState(() => _isPressed = false),
      onTapCancel: () => setState(() => _isPressed = false),
      onTap: isEnabled ? widget.onTap : null,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 100),
        width: widget.width ?? double.infinity,
        height: 60,
        transform: _isPressed ? Matrix4.translationValues(3, 3, 0) : Matrix4.identity(),
        decoration: BrutalistStyle.containerDecoration(
          color: isEnabled ? widget.color : BrutalistColors.gray,
          hasShadow: !_isPressed && isEnabled,
        ),
        alignment: Alignment.center,
        child: widget.isLoading
            ? const SizedBox(
                height: 24,
                width: 24,
                child: CircularProgressIndicator(
                  strokeWidth: 3,
                  valueColor: AlwaysStoppedAnimation<Color>(BrutalistColors.black),
                ),
              )
            : Text(
                widget.text.toUpperCase(),
                style: BrutalistStyle.title(
                  color: isEnabled && widget.color == BrutalistColors.black ? Colors.white : Colors.black,
                ).copyWith(fontSize: 18),
              ),
      ),
    );
  }
}
