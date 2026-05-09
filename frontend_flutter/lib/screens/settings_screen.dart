import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../providers/booking_provider.dart';
import '../theme/brutalist_style.dart';
import '../models/models.dart';
import 'history_screen.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({Key? key}) : super(key: key);

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  @override
  Widget build(BuildContext context) {
    final auth = Provider.of<AuthProvider>(context);
    final booking = Provider.of<BookingProvider>(context);

    return Scaffold(
      backgroundColor: AppleColors.bg,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: AppleColors.black),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text("Account Settings", style: AppleStyle.title()),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _SettingsSection(
              title: "PROFILE",
              items: [
                _SettingsItem(
                  icon: Icons.phone_iphone,
                  title: "Phone Number",
                  subtitle: auth.phone ?? "Not Logged In",
                  onTap: () {},
                ),
                _SettingsItem(
                  icon: Icons.history,
                  title: "Travel History",
                  subtitle: "Your past tickets and routes",
                  onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const HistoryScreen())),
                ),
              ],
            ),
            const SizedBox(height: 32),
            _SettingsSection(
              title: "PREFERENCES",
              items: [
                _SettingsItem(
                  icon: Icons.star_outline,
                  title: "Saved Stations",
                  subtitle: "${booking.recentStations.length} stations cached",
                  onTap: () {
                     ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text("Frequent stations are automatically prioritized."))
                    );
                  },
                ),
                _SettingsItem(
                  icon: Icons.notifications_none,
                  title: "Notification Settings",
                  subtitle: "Enabled",
                  onTap: () {},
                ),
              ],
            ),
            const SizedBox(height: 32),
            _SettingsSection(
              title: "LEGAL & SUPPORT",
              items: [
                _SettingsItem(
                  icon: Icons.help_outline,
                  title: "Help Center",
                  subtitle: "support@pandatern.tech",
                  onTap: () {},
                ),
                _SettingsItem(
                  icon: Icons.shield_outlined,
                  title: "Privacy Policy",
                  subtitle: "Terms of Service",
                  onTap: () {},
                ),
              ],
            ),
            const SizedBox(height: 48),
            GestureDetector(
              onTap: () {
                auth.logout();
                Navigator.popUntil(context, (route) => route.isFirst);
              },
              child: Container(
                width: double.infinity,
                padding: const EdgeInsets.symmetric(vertical: 18),
                decoration: AppleStyle.cardDecoration(color: Colors.white),
                alignment: Alignment.center,
                child: const Text("Sign Out", style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold, fontSize: 17)),
              ),
            ),
            const SizedBox(height: 32),
            const Center(
              child: Text("MAKCO v2.1.6 (Production)", style: TextStyle(color: Colors.grey, fontSize: 12)),
            ),
            const SizedBox(height: 40),
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
          padding: const EdgeInsets.only(left: 12, bottom: 8),
          child: Text(title, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w800, color: Colors.grey, letterSpacing: 0.5)),
        ),
        Container(
          decoration: AppleStyle.cardDecoration(hasShadow: false),
          child: Column(
            children: List.generate(items.length, (index) {
              return Column(
                children: [
                  items[index],
                  if (index < items.length - 1)
                    const Padding(
                      padding: EdgeInsets.only(left: 56),
                      child: Divider(height: 1, color: Color(0xFFF2F2F7)),
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
      leading: Icon(icon, color: AppleColors.black, size: 24),
      title: Text(title, style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 16)),
      subtitle: Text(subtitle, style: const TextStyle(color: Colors.grey, fontSize: 13)),
      trailing: const Icon(Icons.arrow_forward_ios, size: 14, color: Colors.grey),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
    );
  }
}
