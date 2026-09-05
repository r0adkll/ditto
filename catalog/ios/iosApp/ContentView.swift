import SwiftUI
import CatalogKit

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all, edges: .all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context _: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_: UIViewController, context _: Context) {}
}
