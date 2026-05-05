import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:url_launcher/url_launcher.dart';
import '../theme/brutalist_style.dart';

class PaymentWebView extends StatefulWidget {
  final String paymentUrl;
  final VoidCallback onSuccess;
  final VoidCallback onCancel;

  const PaymentWebView({
    Key? key,
    required this.paymentUrl,
    required this.onSuccess,
    required this.onCancel,
  }) : super(key: key);

  @override
  _PaymentWebViewState createState() => _PaymentWebViewState();
}

class _PaymentWebViewState extends State<PaymentWebView> {
  late final WebViewController _controller;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setNavigationDelegate(
        NavigationDelegate(
          onPageStarted: (String url) {
            setState(() => _isLoading = true);
            _handleUrl(url);
          },
          onPageFinished: (String url) {
            setState(() => _isLoading = false);
            _handleUrl(url);
          },
          onNavigationRequest: (NavigationRequest request) {
            final url = request.url;
            
            // Handle UPI Deep Links
            if (url.startsWith('upi://') || 
                url.startsWith('tez://') || 
                url.startsWith('phonepe://') || 
                url.startsWith('paytmmp://')) {
              _launchUPI(url);
              return NavigationDecision.prevent;
            }
            
            _handleUrl(url);
            return NavigationDecision.navigate;
          },
        ),
      )
      ..loadRequest(Uri.parse(widget.paymentUrl));
  }

  void _handleUrl(String url) {
    if (url.contains("ticketBookingStatus") ||
        url.contains("payment/success") ||
        url.contains("status=CHARGED") ||
        url.contains("status=CONFIRMED")) {
      widget.onSuccess();
    } else if (url.contains("payment/cancel") ||
               url.contains("status=CANCELLED") ||
               url.contains("status=FAILED") ||
               url.contains("status=JUSPAY_DECLINED")) {
      widget.onCancel();
    }
  }

  Future<void> _launchUPI(String url) async {
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    } else {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("No UPI app found", style: BrutalistStyle.body(color: Colors.white)),
            backgroundColor: BrutalistColors.error,
          )
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: BrutalistColors.gray,
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back, color: Colors.black),
          onPressed: widget.onCancel,
        ),
        title: Text("PAYMENT", style: BrutalistStyle.title()),
      ),
      body: Stack(
        children: [
          Container(
            decoration: BrutalistStyle.containerDecoration(hasShadow: false),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(BrutalistStyle.borderRadius),
              child: WebViewWidget(controller: _controller),
            ),
          ),
          if (_isLoading)
            const Center(
              child: CircularProgressIndicator(color: BrutalistColors.black),
            ),
        ],
      ),
    );
  }
}
