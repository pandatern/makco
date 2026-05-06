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
      setState(() { _error = "Connection error"; });
    }
    setState(() { _isLoading = false; });
  }

  void _verifyOtp() async {
    if (_otpController.text.length < 4 || _authId == null) return;
    setState(() { _isLoading = true; _error = null; });
    
    final auth = Provider.of<AuthProvider>(context, listen: false);
    final success = await auth.verifyOtp(_authId!, _otpController.text);
    
    if (!success) {
      setState(() { _error = "Invalid code. Please try again."; _isLoading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppleColors.bg,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 60),
              Text(
                _otpSent ? "Verification" : "Welcome",
                style: AppleStyle.largeTitle(),
              ),
              const SizedBox(height: 8),
              Text(
                _otpSent 
                  ? "Enter the 4-digit code sent to you." 
                  : "Sign in with your phone number to continue.",
                style: AppleStyle.body(color: AppleColors.gray),
              ),
              const SizedBox(height: 48),
              if (!_otpSent) ...[
                Container(
                  decoration: AppleStyle.cardDecoration(hasShadow: false),
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                  child: Row(
                    children: [
                      Text("+91", style: AppleStyle.body(bold: true)),
                      const SizedBox(width: 12),
                      Expanded(
                        child: TextField(
                          controller: _phoneController,
                          keyboardType: TextInputType.phone,
                          maxLength: 10,
                          style: AppleStyle.body(bold: true),
                          decoration: const InputDecoration(
                            hintText: "Phone Number",
                            border: InputBorder.none,
                            counterText: "",
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 32),
                AppleButton(
                  text: "Continue",
                  isLoading: _isLoading,
                  onTap: _sendOtp,
                ),
              ] else ...[
                Container(
                  decoration: AppleStyle.cardDecoration(hasShadow: false),
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
                  child: TextField(
                    controller: _otpController,
                    keyboardType: TextInputType.number,
                    maxLength: 4,
                    textAlign: TextAlign.center,
                    style: AppleStyle.largeTitle().copyWith(letterSpacing: 24),
                    autofocus: true,
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
                AppleButton(
                  text: "Verify",
                  isLoading: _isLoading,
                  onTap: _verifyOtp,
                ),
                Center(
                  child: TextButton(
                    onPressed: () => setState(() => _otpSent = false),
                    child: Text("Change Phone Number", style: AppleStyle.body(color: AppleColors.blue)),
                  ),
                ),
              ],
              if (_error != null) ...[
                const SizedBox(height: 16),
                Center(
                  child: Text(
                    _error!,
                    style: AppleStyle.body(color: AppleColors.error),
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}
