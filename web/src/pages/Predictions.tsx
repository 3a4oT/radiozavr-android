import { useState } from "react";

export function Predictions() {
	const [texts, setTexts] = useState<string[]>([
		"Сьогодні зірки пророкують вам гарний настрій!",
		"День сповнений несподіванок та приємних зустрічей.",
		"Ваша інтуїція сьогодні особливо сильна.",
	]);
	const [newText, setNewText] = useState("");

	const addPrediction = () => {
		if (newText.trim()) {
			setTexts((prev) => [...prev, newText.trim()]);
			setNewText("");
		}
	};

	const removePrediction = (index: number) => {
		setTexts((prev) => prev.filter((_, i) => i !== index));
	};

	return (
		<div>
			<div className="mb-6">
				<h1 className="text-2xl font-bold text-gray-800">Predictions</h1>
				<p className="text-gray-500 mt-1">
					App will show one random prediction per day
				</p>
			</div>

			{/* Add new */}
			<div className="bg-white rounded-lg shadow p-4 mb-6">
				<div className="flex gap-3">
					<input
						type="text"
						value={newText}
						onChange={(e) => setNewText(e.target.value)}
						placeholder="Enter new prediction text..."
						className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
						maxLength={500}
					/>
					<button
						type="button"
						onClick={addPrediction}
						disabled={!newText.trim()}
						className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors"
					>
						Add
					</button>
				</div>
				<p className="text-sm text-gray-400 mt-2">{newText.length}/500</p>
			</div>

			{/* List */}
			<div className="bg-white rounded-lg shadow">
				<div className="p-4 border-b">
					<span className="font-medium text-gray-700">
						{texts.length} prediction{texts.length !== 1 ? "s" : ""}
					</span>
				</div>

				{texts.length === 0 ? (
					<div className="p-8 text-center text-gray-500">
						No predictions yet. Add at least one!
					</div>
				) : (
					<ul className="divide-y">
						{texts.map((text, index) => (
							<li
								key={`${index}-${text.substring(0, 20)}`}
								className="p-4 flex items-start justify-between gap-4"
							>
								<span className="text-gray-700">{text}</span>
								<button
									type="button"
									onClick={() => removePrediction(index)}
									className="text-red-500 hover:text-red-700 shrink-0"
								>
									Remove
								</button>
							</li>
						))}
					</ul>
				)}
			</div>
		</div>
	);
}
