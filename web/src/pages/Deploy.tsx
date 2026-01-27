import { useState } from "react";
import { useAuth } from "../lib/useAuth";

export function Deploy() {
	const { user } = useAuth();
	const [isDeploying, setIsDeploying] = useState(false);
	const [lastDeploy, setLastDeploy] = useState<{
		by: string;
		at: string;
	} | null>({
		by: "user@example.com",
		at: "2026-01-27T14:30:00Z",
	});

	const handleDeploy = async () => {
		if (!confirm("Deploy changes to production?")) return;

		setIsDeploying(true);

		// TODO: Implement actual Firebase Remote Config deployment
		await new Promise((resolve) => setTimeout(resolve, 2000));

		setLastDeploy({
			by: user?.email || "unknown",
			at: new Date().toISOString(),
		});
		setIsDeploying(false);

		alert("Deployed successfully!");
	};

	const formatDate = (iso: string) => {
		return new Date(iso).toLocaleString("uk-UA", {
			day: "2-digit",
			month: "2-digit",
			year: "numeric",
			hour: "2-digit",
			minute: "2-digit",
		});
	};

	return (
		<div>
			<h1 className="text-2xl font-bold text-gray-800 mb-6">
				Deploy to Production
			</h1>

			<div className="bg-white rounded-lg shadow p-6">
				<div className="flex items-start gap-4 mb-6">
					<div className="p-3 bg-yellow-100 rounded-full">
						<svg
							className="w-6 h-6 text-yellow-600"
							fill="none"
							viewBox="0 0 24 24"
							stroke="currentColor"
						>
							<title>Warning</title>
							<path
								strokeLinecap="round"
								strokeLinejoin="round"
								strokeWidth={2}
								d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
							/>
						</svg>
					</div>
					<div>
						<h2 className="font-semibold text-gray-800">
							Review before deploying
						</h2>
						<p className="text-gray-500 mt-1">
							This will update the live app configuration. Make sure all changes
							are correct.
						</p>
					</div>
				</div>

				{/* Changes summary - placeholder */}
				<div className="bg-gray-50 rounded-lg p-4 mb-6">
					<h3 className="font-medium text-gray-700 mb-2">Pending changes:</h3>
					<ul className="text-sm text-gray-600 space-y-1">
						<li>• Ticker: Local changes not synced</li>
						<li>• Predictions: Local changes not synced</li>
						<li>• Settings: Local changes not synced</li>
					</ul>
				</div>

				{lastDeploy && (
					<p className="text-sm text-gray-500 mb-6">
						Last deployed: {formatDate(lastDeploy.at)} by {lastDeploy.by}
					</p>
				)}

				<div className="flex gap-4">
					<button
						type="button"
						onClick={handleDeploy}
						disabled={isDeploying}
						className="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors font-medium"
					>
						{isDeploying ? "Deploying..." : "Deploy to Production"}
					</button>
				</div>
			</div>
		</div>
	);
}
