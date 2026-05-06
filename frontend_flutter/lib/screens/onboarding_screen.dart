import 'package:flutter/material.dart';
import '../theme/brutalist_style.dart';
import '../widgets/brutalist_button.dart';

class OnboardingPageData {
  final String title;
  final String subtitle;
  final String description;

  OnboardingPageData({
    required this.title,
    required this.subtitle,
    required this.description,
  });
}

final List<OnboardingPageData> onboardingPages = [
  OnboardingPageData(
    title: "Metro Made Simple",
    subtitle: "41 STATIONS",
    description: "Navigate Chennai Metro effortlessly. Every station, every line, right in your pocket.",
  ),
  OnboardingPageData(
    title: "Book in Seconds",
    subtitle: "INSTANT FARES",
    description: "Get instant fare quotes. Choose your journey. Pay seamlessly with UPI.",
  ),
  OnboardingPageData(
    title: "Skip the Queue",
    subtitle: "QR TICKETS",
    description: "Your digital ticket works at every gate. Just scan and ride.",
  ),
];

class OnboardingScreen extends StatefulWidget {
  final VoidCallback onFinished;

  const OnboardingScreen({Key? key, required this.onFinished}) : super(key: key);

  @override
  State<OnboardingScreen> createState() => _OnboardingScreenState();
}

class _OnboardingScreenState extends State<OnboardingScreen> {
  final PageController _pageController = PageController();
  int _currentPage = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppleColors.white,
      body: SafeArea(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.all(24),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text("MAKCO", style: AppleStyle.body(bold: true).copyWith(letterSpacing: 4)),
                  TextButton(
                    onPressed: widget.onFinished,
                    child: Text("Skip", style: AppleStyle.body(color: AppleColors.gray)),
                  ),
                ],
              ),
            ),
            Expanded(
              child: PageView.builder(
                controller: _pageController,
                onPageChanged: (page) => setState(() => _currentPage = page),
                itemCount: onboardingPages.length,
                itemBuilder: (ctx, i) {
                  final data = onboardingPages[i];
                  return Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 40),
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Container(
                          width: 120,
                          height: 120,
                          decoration: BoxDecoration(
                            color: AppleColors.bg,
                            borderRadius: BorderRadius.circular(32),
                          ),
                          alignment: Alignment.center,
                          child: Text(
                            "${i + 1}",
                            style: AppleStyle.largeTitle().copyWith(fontSize: 48),
                          ),
                        ),
                        const SizedBox(height: 48),
                        Text(
                          data.title,
                          style: AppleStyle.largeTitle().copyWith(fontSize: 28),
                          textAlign: TextAlign.center,
                        ),
                        const SizedBox(height: 16),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                          decoration: BoxDecoration(
                            color: AppleColors.blue.withOpacity(0.1),
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            data.subtitle,
                            style: AppleStyle.body(bold: true, color: AppleColors.blue),
                          ),
                        ),
                        const SizedBox(height: 24),
                        Text(
                          data.description,
                          style: AppleStyle.body(color: AppleColors.gray),
                          textAlign: TextAlign.center,
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 24),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: List.generate(onboardingPages.length, (index) {
                  return AnimatedContainer(
                    duration: const Duration(milliseconds: 300),
                    margin: const EdgeInsets.symmetric(horizontal: 4),
                    width: _currentPage == index ? 24 : 8,
                    height: 8,
                    decoration: BoxDecoration(
                      color: _currentPage == index ? AppleColors.blue : AppleColors.lightGray,
                      borderRadius: BorderRadius.circular(4),
                    ),
                  );
                }),
              ),
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 40, vertical: 48),
              child: AppleButton(
                text: _currentPage == onboardingPages.length - 1 ? "Get Started" : "Continue",
                onTap: () {
                  if (_currentPage < onboardingPages.length - 1) {
                    _pageController.nextPage(
                      duration: const Duration(milliseconds: 400),
                      curve: Curves.easeInOut,
                    );
                  } else {
                    widget.onFinished();
                  }
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
