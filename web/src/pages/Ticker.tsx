import { useState } from "react";
import type { TickerMessage, TickerType } from "../models/schema";

// Placeholder - will be replaced with Firebase integration
const mockTickers: TickerMessage[] = [
	{
		id: "1",
		text: "Welcome to Radio Lux FM!",
		type: "persistent",
		priority: 1,
		active: true,
	},
];

export function Ticker() {
	const [tickers, setTickers] = useState<TickerMessage[]>(mockTickers);
	const [isModalOpen, setIsModalOpen] = useState(false);

	const toggleActive = (id: string) => {
		setTickers((prev) =>
			prev.map((t) => (t.id === id ? { ...t, active: !t.active } : t)),
		);
	};

	const deleteTicker = (id: string) => {
		if (confirm("Delete this message?")) {
			setTickers((prev) => prev.filter((t) => t.id !== id));
		}
	};

	const getTypeColor = (type: TickerType) => {
		switch (type) {
			case "persistent":
				return "bg-purple-100 text-purple-700";
			case "repeat":
				return "bg-blue-100 text-blue-700";
			case "once":
				return "bg-gray-100 text-gray-700";
		}
	};

	return (
		<div>
			<div className="flex items-center justify-between mb-6">
				<h1 className="text-2xl font-bold text-gray-800">Ticker Messages</h1>
				<button
					type="button"
					onClick={() => setIsModalOpen(true)}
					className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
				>
					+ Add Message
				</button>
			</div>

			{tickers.length === 0 ? (
				<div className="text-center py-12 text-gray-500">
					No ticker messages yet. Add your first one!
				</div>
			) : (
				<div className="space-y-4">
					{tickers.map((ticker) => (
						<div
							key={ticker.id}
							className={`bg-white rounded-lg shadow p-4 border-l-4 ${
								ticker.active ? "border-green-500" : "border-gray-300"
							}`}
						>
							<div className="flex items-start justify-between">
								<div className="flex-1">
									<p className="text-gray-800 font-medium">{ticker.text}</p>
									<div className="flex items-center gap-2 mt-2">
										<span
											className={`px-2 py-1 text-xs rounded-full ${getTypeColor(ticker.type)}`}
										>
											{ticker.type}
										</span>
										<span className="text-sm text-gray-500">
											Priority: {ticker.priority}
										</span>
										{!ticker.active && (
											<span className="text-sm text-red-500">(inactive)</span>
										)}
									</div>
								</div>
								<div className="flex items-center gap-2">
									<button
										type="button"
										onClick={() => toggleActive(ticker.id)}
										className={`px-3 py-1 text-sm rounded ${
											ticker.active
												? "bg-gray-100 text-gray-600"
												: "bg-green-100 text-green-600"
										}`}
									>
										{ticker.active ? "Deactivate" : "Activate"}
									</button>
									<button
										type="button"
										onClick={() => deleteTicker(ticker.id)}
										className="px-3 py-1 text-sm bg-red-100 text-red-600 rounded hover:bg-red-200"
									>
										Delete
									</button>
								</div>
							</div>
						</div>
					))}
				</div>
			)}

			{/* TODO: Add modal form */}
			{isModalOpen && (
				<div className="fixed inset-0 bg-black/50 flex items-center justify-center">
					<div className="bg-white rounded-xl p-6 max-w-md w-full mx-4">
						<h2 className="text-xl font-bold mb-4">Add Ticker Message</h2>
						<p className="text-gray-500">Form coming soon...</p>
						<button
							type="button"
							onClick={() => setIsModalOpen(false)}
							className="mt-4 px-4 py-2 bg-gray-100 rounded-lg"
						>
							Close
						</button>
					</div>
				</div>
			)}
		</div>
	);
}
