import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.PrintWriter;

/**
 * Writes the download QR as an SVG. Run with scripts/make-site-qr.sh; it is not part of
 * the site build, so the page ships a static file and needs no runtime dependency.
 */
public class QrSvg {
    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : "https://omnishelf.cc/go/play/?src=qr";
        int module = args.length > 1 ? Integer.parseInt(args[1]) : 8;
        String out = args.length > 2 ? args[2] : "public/qr/play.svg";
        BitMatrix m = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 0, 0);
        int n = m.getWidth();
        int size = (n + 8) * module;             // four-module quiet zone, as scanners expect
        StringBuilder d = new StringBuilder();
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                if (m.get(x, y)) {
                    int px = (x + 4) * module, py = (y + 4) * module;
                    d.append('M').append(px).append(' ').append(py)
                     .append('h').append(module).append('v').append(module)
                     .append('h').append(-module).append('z');
                }
            }
        }
        try (PrintWriter w = new PrintWriter(out, "UTF-8")) {
            w.println("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"" + size + "\" height=\"" + size
                    + "\" viewBox=\"0 0 " + size + " " + size + "\" role=\"img\" aria-label=\"Download OmniShelf\">");
            w.println("  <rect width=\"" + size + "\" height=\"" + size + "\" fill=\"#fbf8f2\"/>");
            w.println("  <path fill=\"#201814\" shape-rendering=\"crispEdges\" d=\"" + d + "\"/>");
            w.println("</svg>");
        }
        System.out.println("wrote " + out + " (" + size + "px) for " + url);
    }
}
