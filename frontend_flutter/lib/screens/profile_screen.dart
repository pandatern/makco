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
      backgroundColor: BrutalistColors.white,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.black),
          onPressed: () => Navigator.pop(context),
        ),
        title: Text("PROFILE", style: BrutalistStyle.title()),
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(32),
              decoration: BrutalistStyle.containerDecoration(color: BrutalistColors.gray),
              child: Column(
                children: [
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BrutalistStyle.containerDecoration(radius: 40, color: BrutalistColors.primary),
                    alignment: Alignment.Center,
                    child: Text("U", style: BrutalistStyle.heading(color: Colors.white)),
                  ),
                  const SizedBox(height: 16),
                  Text("USER ACCOUNT", style: BrutalistStyle.title()),
                  const SizedBox(height: 8),
                  Text("Active Session", style: BrutalistStyle.label(color: Colors.grey)),
                ],
              ),
            ),
            const SizedBox(height: 32),
            GestureDetector(
              onTap: () {
                auth.logout();
                Navigator.pop(context);
              },
              child: Container(
                width: double.infinity,
                padding: const EdgeInsets.symmetric(vertical: 20),
                decoration: BrutalistStyle.containerDecoration(color: BrutalistColors.error),
                alignment: Alignment.Center,
                child: Text("LOGOUT", style: BrutalistStyle.title(color: Colors.white)),
              ),
            ),
            const Spacer(),
            Text("MAKCO v2.0", style: BrutalistStyle.label(color: Colors.grey)),
            const SizedBox(height: 24),
          ],
        ),
      ),
    );
  }
}
