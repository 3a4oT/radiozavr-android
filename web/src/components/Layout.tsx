import { signOut } from "firebase/auth";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { auth } from "../lib/firebase";
import { useAuth } from "../lib/useAuth";

const navItems = [
	{ to: "/ticker", label: "Ticker" },
	{ to: "/predictions", label: "Predictions" },
	{ to: "/settings", label: "Settings" },
	{ to: "/deploy", label: "Deploy" },
];

export function Layout() {
	const { user } = useAuth();
	const navigate = useNavigate();

	const handleLogout = async () => {
		await signOut(auth);
		navigate("/login");
	};

	return (
		<div className="flex h-screen bg-gray-100">
			{/* Sidebar */}
			<aside className="w-64 bg-white shadow-md flex flex-col">
				<div className="p-4 border-b">
					<h1 className="text-xl font-bold text-gray-800">Radio Lux Admin</h1>
					<p className="text-sm text-gray-500 truncate">{user?.email}</p>
				</div>

				<nav className="flex-1 p-4">
					<ul className="space-y-2">
						{navItems.map((item) => (
							<li key={item.to}>
								<NavLink
									to={item.to}
									className={({ isActive }) =>
										`block px-4 py-2 rounded-lg transition-colors ${
											isActive
												? "bg-blue-100 text-blue-700 font-medium"
												: "text-gray-700 hover:bg-gray-100"
										}`
									}
								>
									{item.label}
								</NavLink>
							</li>
						))}
					</ul>
				</nav>

				<div className="p-4 border-t">
					<button
						type="button"
						onClick={handleLogout}
						className="w-full px-4 py-2 text-sm text-gray-600 hover:text-gray-800 hover:bg-gray-100 rounded-lg transition-colors"
					>
						Logout
					</button>
				</div>
			</aside>

			{/* Main content */}
			<main className="flex-1 overflow-auto p-8">
				<Outlet />
			</main>
		</div>
	);
}
