import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../theme/brutalist_style.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final auth = Provider.of<AuthProvider>(context, listen: false);
    
    return Scaffold(
      backgroundColor: AppleColors.bg,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: AppleColors.black),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text("User Profile", style: AppleStyle.title()),
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(32),
              decoration: AppleStyle.cardDecoration(),
              child: Column(
                children: [
                  Container(
                    width: 80,
                    height: 80,
                    decoration: const BoxDecoration(
                      color: AppleColors.black,
                      shape: BoxShape.circle,
                    ),
                    alignment: Alignment.center,
                    child: const Icon(Icons.person, size: 40, color: Colors.white),
                  ),
                  const SizedBox(height: 20),
                  Text("MEMBER", style: AppleStyle.title()),
                  const SizedBox(height: 8),
                  Text("Verified Account", style: AppleStyle.footnote()),
                ],
              ),
            ),
            const SizedBox(height: 32),
            _ProfileItem(
              icon: Icons.phone_android_outlined,
              label: "Mobile Number",
              value: auth.phone ?? "Not available",
            ),
            const SizedBox(height: 12),
            _ProfileItem(
              icon: Icons.verified_user_outlined,
              label: "Identity Status",
              value: "Authentication Successful",
              valueColor: Colors.black,
            ),
            const Spacer(),
            Text("MAKCO v2.1.3", style: AppleStyle.footnote()),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }
}

class _ProfileItem extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color? valueColor;

  const _ProfileItem({
    required this.icon,
    required this.label,
    required this.value,
    this.valueColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: AppleStyle.cardDecoration(),
      child: Row(
        children: [
          Icon(icon, color: AppleColors.black, size: 24),
          const SizedBox(width: 16),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: AppleStyle.footnote().copyWith(fontSize: 11)),
              const SizedBox(height: 4),
              Text(value, style: AppleStyle.body(bold: true, color: valueColor ?? AppleColors.black)),
            ],
          ),
        ],
      ),
    );
  }
}
