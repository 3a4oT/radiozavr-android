import { z } from "zod/v4";

// Ticker message types
export const TickerTypeSchema = z.enum(["once", "repeat", "persistent"]);
export type TickerType = z.infer<typeof TickerTypeSchema>;

// Ticker message
export const TickerMessageSchema = z
	.object({
		id: z.string().uuid(),
		text: z.string().min(1, "Text is required").max(200, "Max 200 characters"),
		type: TickerTypeSchema,
		priority: z.number().int().min(1).max(10),
		active: z.boolean(),
		startDate: z.string().datetime().optional(),
		endDate: z.string().datetime().optional(),
	})
	.refine(
		(data) => !data.endDate || !data.startDate || data.endDate > data.startDate,
		{ message: "End date must be after start date" },
	);

export type TickerMessage = z.infer<typeof TickerMessageSchema>;

// Predictions - array of texts, app picks randomly per day
export const PredictionsSchema = z.object({
	texts: z
		.array(z.string().min(1, "Text is required").max(500, "Max 500 characters"))
		.min(1, "At least one prediction required"),
});

export type Predictions = z.infer<typeof PredictionsSchema>;

// Stream config
export const StreamConfigSchema = z.object({
	url: z.string().url("Invalid URL"),
	backupUrl: z.string().url("Invalid URL").optional(),
});

export type StreamConfig = z.infer<typeof StreamConfigSchema>;

// Contact config
export const ContactConfigSchema = z.object({
	studioPhone: z.string().min(1, "Phone is required"),
	viberDeepLink: z.string().url("Invalid URL").optional(),
});

export type ContactConfig = z.infer<typeof ContactConfigSchema>;

// Feature flags
export const FeatureFlagsSchema = z.object({
	showWeather: z.boolean(),
	showGeomagnetic: z.boolean(),
	showPredictions: z.boolean(),
	showTicker: z.boolean(),
});

export type FeatureFlags = z.infer<typeof FeatureFlagsSchema>;

// Update config
export const UpdateConfigSchema = z.object({
	minVersion: z
		.string()
		.regex(/^\d+\.\d+\.\d+$/, "Must be semver format (X.Y.Z)"),
	forceUpdate: z.boolean(),
});

export type UpdateConfig = z.infer<typeof UpdateConfigSchema>;

// Full app config
export const AppConfigSchema = z.object({
	ticker: z.array(TickerMessageSchema),
	predictions: PredictionsSchema,
	stream: StreamConfigSchema,
	contact: ContactConfigSchema,
	features: FeatureFlagsSchema,
	update: UpdateConfigSchema,
	_meta: z.object({
		lastModifiedBy: z.string().email(),
		lastModifiedAt: z.string().datetime(),
	}),
});

export type AppConfig = z.infer<typeof AppConfigSchema>;

// Default config
export const defaultConfig: AppConfig = {
	ticker: [],
	predictions: { texts: ["Гарного дня!"] },
	stream: {
		url: "http://streamvideo.luxnet.ua/luxlviv/luxlviv.stream/chunklist.m3u8",
	},
	contact: { studioPhone: "+380971047007" },
	features: {
		showWeather: true,
		showGeomagnetic: true,
		showPredictions: true,
		showTicker: true,
	},
	update: { minVersion: "0.1.0", forceUpdate: false },
	_meta: {
		lastModifiedBy: "",
		lastModifiedAt: new Date().toISOString(),
	},
};
