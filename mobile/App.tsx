import { useEffect, useState } from 'react';
import { ActivityIndicator, StyleSheet, View } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { clearToken, fetchMe, getToken, type LoginResponse } from './src/api';
import LoginScreen from './src/LoginScreen';
import DashboardScreen from './src/DashboardScreen';

export default function App() {
  const [checking, setChecking] = useState(true);
  const [user, setUser] = useState<LoginResponse | null>(null);

  useEffect(() => {
    (async () => {
      const token = await getToken();
      if (!token) {
        setChecking(false);
        return;
      }
      try {
        const me = await fetchMe();
        setUser({ token, id: me.id, name: me.name, orgnztNm: me.orgnztNm, userSe: me.userSe });
      } catch {
        await clearToken();
      } finally {
        setChecking(false);
      }
    })();
  }, []);

  if (checking) {
    return (
      <View style={styles.loading}>
        <ActivityIndicator size="large" />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {user ? (
        <DashboardScreen user={user} onLoggedOut={() => setUser(null)} />
      ) : (
        <LoginScreen onLoggedIn={setUser} />
      )}
      <StatusBar style="auto" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  loading: { flex: 1, justifyContent: 'center', alignItems: 'center' },
});
