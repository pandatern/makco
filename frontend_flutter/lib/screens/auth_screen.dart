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
      setState(() { _error = "Invalid OTP"; _isLoading = false; });
    }
    // AuthProvider will trigger notifyListeners, main.dart will handle navigation
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: BrutalistColors.white,
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 32),
        child: Column(
          crossAxisAlignment: Navigator.canPop(context) ? CrossAxisAlignment.start : CrossAxisAlignment.center,
          children: [
            const SizedBox(height: 80),
            Center(
              child: Column(
                children: [
                  Container(
                    width: 80,
                    height: 80,
                    decoration: BrutalistStyle.containerDecoration(color: BrutalistColors.primary),
                    alignment: Alignment.center,
                    child: Text("M", style: BrutalistStyle.heading(color: Colors.white).copyWith(fontSize: 48)),
                  ),
                  const SizedBox(height: 24),
                  Text("MAKCO", style: BrutalistStyle.heading(color: BrutalistColors.black).copyWith(letterSpacing: 4)),
                  Text("CHENNAI METRO", style: BrutalistStyle.label(color: BrutalistColors.darkGray)),
                ],
              ),
            ),
            const SizedBox(height: 64),
            Text(_otpSent ? "ENTER CODE" : "ENTER PHONE", style: BrutalistStyle.title()),
            const SizedBox(height: 16),
            if (!_otpSent) ...[
              Container(
                decoration: BrutalistStyle.containerDecoration(hasShadow: false),
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Row(
                  children: [
                    Text("+91", style: BrutalistStyle.title()),
                    const SizedBox(width: 12),
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
                decoration: BrutalistStyle.containerDecoration(hasShadow: false),
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: TextField(
                  controller: _otpController,
                  keyboardType: TextInputType.number,
                  maxLength: 4,
                  textAlign: TextAlign.center,
                  style: BrutalistStyle.heading().copyWith(letterSpacing: 20),
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
                text: "Verify",
                isLoading: _isLoading,
                onTap: _verifyOtp,
              ),
              const SizedBox(height: 16),
              TextButton(
                onPressed: () => setState(() => _otpSent = false),
                child: Text("CHANGE NUMBER", style: BrutalistStyle.label(color: BrutalistColors.primary)),
              ),
            ],
            if (_error != null) ...[
              const SizedBox(height: 16),
              Text(_error!, style: BrutalistStyle.body(color: BrutalistColors.error, bold: true)),
            ],
            const Spacer(),
            Center(
              child: Text("By continuing, you agree to our Terms", style: BrutalistStyle.label(color: Colors.grey)),
            ),
            const SizedBox(height: 48),
          ],
        ),
      ),
    );
  }
}
