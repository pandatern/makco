import 'package:flutter/material.dart';
import '../theme/brutalist_style.dart';

class BrutalistButton extends StatefulWidget {
  final String text;
  final VoidCallback? onTap;
  final Color color;
  final bool isLoading;

  const BrutalistButton({
    Key? key,
    required this.text,
    this.onTap,
    this.color = BrutalistColors.accent,
    this.isLoading = false,
  }) : super(key: key);

  @override
  _BrutalistButtonState createState() => _BrutalistButtonState();
}

class _BrutalistButtonState extends State<BrutalistButton> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 100),
    );
    _scaleAnimation = Tween<double>(begin: 1.0, end: 0.96).animate(_controller);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final bool isEnabled = widget.onTap != null && !widget.isLoading;

    return GestureDetector(
      onTapDown: (_) => isEnabled ? _controller.forward() : null,
      onTapUp: (_) => isEnabled ? _controller.reverse() : null,
      onTapCancel: () => isEnabled ? _controller.reverse() : null,
      onTap: isEnabled ? widget.onTap : null,
      child: ScaleTransition(
        scale: _scaleAnimation,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 100),
          width: double.infinity,
          height: 65,
          decoration: BrutalistStyle.box(
            color: isEnabled ? widget.color : Colors.grey[300]!,
            hasShadow: isEnabled,
          ),
          alignment: Alignment.center,
          child: widget.isLoading
              ? const CircularProgressIndicator(color: Colors.black, strokeWidth: 4)
              : Text(
                  widget.text.toUpperCase(),
                  style: BrutalistStyle.title().copyWith(fontSize: 20),
                ),
        ),
      ),
    );
  }
}
