import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'providers/auth_provider.dart';
import 'providers/booking_provider.dart';
import 'screens/auth_screen.dart';
import 'screens/home_screen.dart';
import 'screens/splash_screen.dart';
import 'screens/onboarding_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final authProvider = AuthProvider();
  await authProvider.init();
  
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: authProvider),
        ChangeNotifierProxyProvider<AuthProvider, BookingProvider>(
          create: (_) => BookingProvider(authProvider.apiService),
          update: (_, auth, booking) => BookingProvider(auth.apiService),
        ),
      ],
      child: const MakcoApp(),
    ),
  );
}

class MakcoApp extends StatefulWidget {
  const MakcoApp({Key? key}) : super(key: key);

  @override
  State<MakcoApp> createState() => _MakcoAppState();
}

class _MakcoAppState extends State<MakcoApp> {
  bool _showSplash = true;
  bool _showOnboarding = false;

  @override
  void initState() {
    super.initState();
    _checkOnboarding();
  }

  Future<void> _checkOnboarding() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      _showOnboarding = !(prefs.getBool('onboarding_done') ?? false);
    });
  }

  void _finishOnboarding() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('onboarding_done', true);
    setState(() => _showOnboarding = false);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Makco v2.1',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor: const Color(0xFFF2F2F7),
      ),
      home: _showSplash 
        ? SplashScreen(onFinished: () => setState(() => _showSplash = false))
        : _showOnboarding
          ? OnboardingScreen(onFinished: _finishOnboarding)
          : Consumer<AuthProvider>(
              builder: (context, auth, _) {
                return auth.isAuthenticated ? const HomeScreen() : const AuthScreen();
              },
            ),
    );
  }
}
