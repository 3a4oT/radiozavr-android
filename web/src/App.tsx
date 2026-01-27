import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { Layout } from "./components/Layout";
import { useAuth } from "./lib/useAuth";
import { Deploy } from "./pages/Deploy";
import { Login } from "./pages/Login";
import { Predictions } from "./pages/Predictions";
import { Settings } from "./pages/Settings";
import { Ticker } from "./pages/Ticker";

function ProtectedRoute({ children }: { children: React.ReactNode }) {
	const { user, loading, isAuthorized } = useAuth();

	if (loading) {
		return (
			<div className="min-h-screen flex items-center justify-center">
				<div className="text-gray-500">Loading...</div>
			</div>
		);
	}

	if (!user || !isAuthorized) {
		return <Navigate to="/login" replace />;
	}

	return <>{children}</>;
}

function App() {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/login" element={<Login />} />
				<Route
					path="/"
					element={
						<ProtectedRoute>
							<Layout />
						</ProtectedRoute>
					}
				>
					<Route index element={<Navigate to="/ticker" replace />} />
					<Route path="ticker" element={<Ticker />} />
					<Route path="predictions" element={<Predictions />} />
					<Route path="settings" element={<Settings />} />
					<Route path="deploy" element={<Deploy />} />
				</Route>
			</Routes>
		</BrowserRouter>
	);
}

export default App;
