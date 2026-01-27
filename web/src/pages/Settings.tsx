import { useState } from "react";

type Tab = "stream" | "contact" | "features" | "update";

export function Settings() {
	const [activeTab, setActiveTab] = useState<Tab>("stream");

	const tabs: { id: Tab; label: string }[] = [
		{ id: "stream", label: "Stream" },
		{ id: "contact", label: "Contact" },
		{ id: "features", label: "Features" },
		{ id: "update", label: "Update" },
	];

	return (
		<div>
			<h1 className="text-2xl font-bold text-gray-800 mb-6">Settings</h1>

			{/* Tabs */}
			<div className="flex gap-2 mb-6">
				{tabs.map((tab) => (
					<button
						key={tab.id}
						type="button"
						onClick={() => setActiveTab(tab.id)}
						className={`px-4 py-2 rounded-lg transition-colors ${
							activeTab === tab.id
								? "bg-blue-600 text-white"
								: "bg-white text-gray-600 hover:bg-gray-100"
						}`}
					>
						{tab.label}
					</button>
				))}
			</div>

			{/* Content */}
			<div className="bg-white rounded-lg shadow p-6">
				{activeTab === "stream" && <StreamSettings />}
				{activeTab === "contact" && <ContactSettings />}
				{activeTab === "features" && <FeaturesSettings />}
				{activeTab === "update" && <UpdateSettings />}
			</div>
		</div>
	);
}

function StreamSettings() {
	const [url, setUrl] = useState(
		"http://streamvideo.luxnet.ua/luxlviv/luxlviv.stream/chunklist.m3u8",
	);
	const [backupUrl, setBackupUrl] = useState("");

	return (
		<div className="space-y-4">
			<div>
				<label
					htmlFor="stream-url"
					className="block text-sm font-medium text-gray-700 mb-1"
				>
					Primary Stream URL *
				</label>
				<input
					id="stream-url"
					type="url"
					value={url}
					onChange={(e) => setUrl(e.target.value)}
					className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
				/>
			</div>
			<div>
				<label
					htmlFor="backup-url"
					className="block text-sm font-medium text-gray-700 mb-1"
				>
					Backup Stream URL
				</label>
				<input
					id="backup-url"
					type="url"
					value={backupUrl}
					onChange={(e) => setBackupUrl(e.target.value)}
					className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
				/>
			</div>
		</div>
	);
}

function ContactSettings() {
	const [phone, setPhone] = useState("+380971047007");
	const [viber, setViber] = useState("");

	return (
		<div className="space-y-4">
			<div>
				<label
					htmlFor="studio-phone"
					className="block text-sm font-medium text-gray-700 mb-1"
				>
					Studio Phone *
				</label>
				<input
					id="studio-phone"
					type="tel"
					value={phone}
					onChange={(e) => setPhone(e.target.value)}
					className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
				/>
			</div>
			<div>
				<label
					htmlFor="viber-link"
					className="block text-sm font-medium text-gray-700 mb-1"
				>
					Viber Deep Link
				</label>
				<input
					id="viber-link"
					type="url"
					value={viber}
					onChange={(e) => setViber(e.target.value)}
					placeholder="viber://chat?number=..."
					className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
				/>
			</div>
		</div>
	);
}

function FeaturesSettings() {
	const [features, setFeatures] = useState({
		showWeather: true,
		showGeomagnetic: true,
		showPredictions: true,
		showTicker: true,
	});

	const toggle = (key: keyof typeof features) => {
		setFeatures((prev) => ({ ...prev, [key]: !prev[key] }));
	};

	const featureItems = [
		{ key: "showWeather" as const, label: "Show Weather" },
		{ key: "showGeomagnetic" as const, label: "Show Geomagnetic" },
		{ key: "showPredictions" as const, label: "Show Predictions" },
		{ key: "showTicker" as const, label: "Show Ticker" },
	];

	return (
		<div className="space-y-4">
			{featureItems.map((item) => (
				<label
					key={item.key}
					className="flex items-center justify-between cursor-pointer"
				>
					<span className="text-gray-700">{item.label}</span>
					<button
						type="button"
						onClick={() => toggle(item.key)}
						className={`relative w-12 h-6 rounded-full transition-colors ${
							features[item.key] ? "bg-blue-600" : "bg-gray-300"
						}`}
					>
						<span
							className={`absolute top-1 w-4 h-4 bg-white rounded-full transition-transform ${
								features[item.key] ? "left-7" : "left-1"
							}`}
						/>
					</button>
				</label>
			))}
		</div>
	);
}

function UpdateSettings() {
	const [minVersion, setMinVersion] = useState("0.1.0");
	const [forceUpdate, setForceUpdate] = useState(false);

	return (
		<div className="space-y-4">
			<div>
				<label
					htmlFor="min-version"
					className="block text-sm font-medium text-gray-700 mb-1"
				>
					Minimum Version (semver)
				</label>
				<input
					id="min-version"
					type="text"
					value={minVersion}
					onChange={(e) => setMinVersion(e.target.value)}
					placeholder="X.Y.Z"
					className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
				/>
			</div>
			<label className="flex items-center justify-between cursor-pointer">
				<span className="text-gray-700">Force Update</span>
				<button
					type="button"
					onClick={() => setForceUpdate(!forceUpdate)}
					className={`relative w-12 h-6 rounded-full transition-colors ${
						forceUpdate ? "bg-red-600" : "bg-gray-300"
					}`}
				>
					<span
						className={`absolute top-1 w-4 h-4 bg-white rounded-full transition-transform ${
							forceUpdate ? "left-7" : "left-1"
						}`}
					/>
				</button>
			</label>
			{forceUpdate && (
				<p className="text-sm text-red-500">
					Warning: Users below minimum version will be forced to update!
				</p>
			)}
		</div>
	);
}
