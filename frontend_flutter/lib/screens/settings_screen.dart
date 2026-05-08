import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../theme/brutalist_style.dart';
import 'history_screen.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({Key? key}) : super(key: key);

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
        title: Text("Settings", style: AppleStyle.title()),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _SettingsSection(
              title: "ACCOUNT",
              items: [
                _SettingsItem(
                  icon: Icons.person_outline,
                  title: "Personal Details",
                  subtitle: auth.phone ?? "Sign in to see details",
                  onTap: () {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text("Phone: ${auth.phone}", style: AppleStyle.body(color: Colors.white)), backgroundColor: AppleColors.black)
                    );
                  },
                ),
                _SettingsItem(
                  icon: Icons.history,
                  title: "Booking History",
                  subtitle: "View all your past tickets",
                  onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const HistoryScreen())),
                ),
              ],
            ),
            const SizedBox(height: 32),
            _SettingsSection(
              title: "SUPPORT",
              items: [
                _SettingsItem(
                  icon: Icons.help_outline,
                  title: "Help & FAQ",
                  subtitle: "How to use the app",
                  onTap: () {},
                ),
                _SettingsItem(
                  icon: Icons.privacy_tip_outlined,
                  title: "Privacy Policy",
                  subtitle: "Data protection rules",
                  onTap: () {},
                ),
              ],
            ),
            const SizedBox(height: 32),
            GestureDetector(
              onTap: () {
                auth.logout();
                Navigator.popUntil(context, (route) => route.isFirst);
              },
              child: Container(
                width: double.infinity,
                padding: const EdgeInsets.symmetric(vertical: 16),
                decoration: AppleStyle.cardDecoration(color: Colors.white),
                alignment: Alignment.center,
                child: Text("Sign Out", style: AppleStyle.body(bold: true, color: AppleColors.error)),
              ),
            ),
            const SizedBox(height: 48),
            Center(
              child: Column(
                children: [
                  Text("MAKCO v2.1.3", style: AppleStyle.footnote().copyWith(fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  Text("© 2026 PANDATERN", style: AppleStyle.footnote()),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SettingsSection extends StatelessWidget {
  final String title;
  final List<_SettingsItem> items;

  const _SettingsSection({required this.title, required this.items});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 16, bottom: 8),
          child: Text(title, style: AppleStyle.footnote().copyWith(fontWeight: FontWeight.bold)),
        ),
        Container(
          decoration: AppleStyle.cardDecoration(),
          child: Column(
            children: List.generate(items.length, (index) {
              return Column(
                children: [
                  items[index],
                  if (index < items.length - 1)
                    const Padding(
                      padding: EdgeInsets.only(left: 56),
                      child: Divider(height: 1, color: AppleColors.bg),
                    ),
                ],
              );
            }),
          ),
        ),
      ],
    );
  }
}

class _SettingsItem extends StatelessWidget {
  final IconData icon;
  final String title;
  final String subtitle;
  final VoidCallback onTap;

  const _SettingsItem({
    required this.icon,
    required this.title,
    required this.subtitle,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      onTap: onTap,
      leading: Container(
        padding: const EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: AppleColors.bg,
          borderRadius: BorderRadius.circular(10),
        ),
        child: Icon(icon, color: AppleColors.black, size: 20),
      ),
      title: Text(title, style: AppleStyle.body(bold: true).copyWith(fontSize: 16)),
      subtitle: Text(subtitle, style: AppleStyle.footnote()),
      trailing: const Icon(Icons.arrow_forward_ios, size: 14, color: AppleColors.lightGray),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
    );
  }
}
