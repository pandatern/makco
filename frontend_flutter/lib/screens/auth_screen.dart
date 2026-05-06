import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../theme/brutalist_style.dart';
import '../widgets/brutalist_button.dart';

class AuthScreen extends StatefulWidget {
  const AuthScreen({Key? key}) : super(key: key);

  @override
  _AuthScreenState createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _otpController = TextEditingController();
  
  bool _otpSent = false;
  bool _isLoading = false;
  String? _authId;
  String? _error;

  void _sendOtp() async {
    if (_phoneController.text.length < 10) return;
    setState(() { _isLoading = true; _error = null; });
    
    try {
      final auth = Provider.of<AuthProvider>(context, listen: false);
      final response = await auth.initiateAuth(_phoneController.text);
      if (response.containsKey('authId')) {
        setState(() {
          _authId = response['authId'];
          _otpSent = true;
        });
      } else {
        setState(() { _error = "Failed to send OTP"; });
      }
    } catch (e) {
      setState(() { _error = "Check internet connection"; });
    }
    setState(() { _isLoading = false; });
  }

  void _verifyOtp() async {
    if (_otpController.text.length < 4 || _authId == null) return;
    setState(() { _isLoading = true; _error = null; });
    
    final auth = Provider.of<AuthProvider>(context, listen: false);
    final success = await auth.verifyOtp(_authId!, _otpController.text);
    
    if (!success) {
      setState(() { _error = "Invalid OTP"; _isLoading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: BrutalistColors.bg,
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 100),
              Container(
                width: double.infinity,
                decoration: BrutalistStyle.box(color: BrutalistColors.accent),
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text("MAKCO", style: BrutalistStyle.heading().copyWith(fontSize: 50)),
                    const SizedBox(height: 8),
                    Text("METRO TICKETING", style: BrutalistStyle.label()),
                  ],
                ),
              ),
              const SizedBox(height: 64),
              Text(_otpSent ? "VERIFY OTP" : "PHONE LOGIN", style: BrutalistStyle.title()),
              const SizedBox(height: 16),
              if (!_otpSent) ...[
                Container(
                  decoration: BrutalistStyle.box(hasShadow: false),
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  child: Row(
                    children: [
                      Text("+91", style: BrutalistStyle.title()),
                      const SizedBox(width: 16),
                      Expanded(
                        child: TextField(
                          controller: _phoneController,
                          keyboardType: TextInputType.phone,
                          maxLength: 10,
                          style: BrutalistStyle.title(),
                          decoration: const InputDecoration(
                            hintText: "00000 00000",
                            border: InputBorder.none,
                            counterText: "",
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 32),
                BrutalistButton(
                  text: "Send OTP",
                  isLoading: _isLoading,
                  onTap: _sendOtp,
                ),
              ] else ...[
                Container(
                  decoration: BrutalistStyle.box(hasShadow: false),
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  child: TextField(
                    controller: _otpController,
                    keyboardType: TextInputType.number,
                    maxLength: 4,
                    textAlign: TextAlign.center,
                    style: BrutalistStyle.heading().copyWith(letterSpacing: 30),
                    decoration: const InputDecoration(
                      hintText: "••••",
                      border: InputBorder.none,
                      counterText: "",
                    ),
                    onChanged: (val) {
                      if (val.length == 4) _verifyOtp();
                    },
                  ),
                ),
                const SizedBox(height: 32),
                BrutalistButton(
                  text: "Verify & Enter",
                  isLoading: _isLoading,
                  onTap: _verifyOtp,
                ),
                Center(
                  child: TextButton(
                    onPressed: () => setState(() => _otpSent = false),
                    child: Text("BACK TO PHONE", style: BrutalistStyle.label().copyWith(decoration: TextDecoration.underline)),
                  ),
                ),
              ],
              if (_error != null) ...[
                const SizedBox(height: 24),
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(16),
                  decoration: BrutalistStyle.box(color: BrutalistColors.error, hasShadow: false),
                  child: Text(_error!, style: BrutalistStyle.body(bold: true).copyWith(color: Colors.white)),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
