import { onAuthStateChanged, type User } from "firebase/auth";
import { useEffect, useState } from "react";
import { auth, isAdmin } from "./firebase";

interface AuthState {
	user: User | null;
	loading: boolean;
	isAuthorized: boolean;
}

export function useAuth(): AuthState {
	const [user, setUser] = useState<User | null>(null);
	const [loading, setLoading] = useState(true);

	useEffect(() => {
		const unsubscribe = onAuthStateChanged(auth, (user) => {
			setUser(user);
			setLoading(false);
		});

		return () => unsubscribe();
	}, []);

	return {
		user,
		loading,
		isAuthorized: user !== null && isAdmin(user.email),
	};
}
