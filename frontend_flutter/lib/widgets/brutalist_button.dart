import 'package:flutter/material.dart';
import '../theme/brutalist_style.dart';

class AppleButton extends StatefulWidget {
  final String text;
  final VoidCallback? onTap;
  final Color color;
  final bool isLoading;

  const AppleButton({
    Key? key,
    required this.text,
    this.onTap,
    this.color = AppleColors.blue,
    this.isLoading = false,
  }) : super(key: key);

  @override
  _AppleButtonState createState() => _AppleButtonState();
}

class _AppleButtonState extends State<AppleButton> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 100),
    );
    _scaleAnimation = Tween<double>(begin: 1.0, end: 0.97).animate(_controller);
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
        child: Container(
          width: double.infinity,
          height: 56,
          decoration: BoxDecoration(
            color: isEnabled ? widget.color : AppleColors.lightGray,
            borderRadius: BorderRadius.circular(16),
          ),
          alignment: Alignment.center,
          child: widget.isLoading
              ? const SizedBox(
                  width: 24,
                  height: 24,
                  child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2.5),
                )
              : Text(
                  widget.text,
                  style: AppleStyle.body(bold: true, color: Colors.white).copyWith(fontSize: 18),
                ),
        ),
      ),
    );
  }
}
