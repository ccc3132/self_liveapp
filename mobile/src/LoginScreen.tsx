import { useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { login, saveToken, type LoginResponse } from './api';

type Props = { onLoggedIn: (user: LoginResponse) => void };

const USER_TYPES: Array<{ label: string; value: string }> = [
  { label: '일반', value: 'GNR' },
  { label: '기업', value: 'ENT' },
  { label: '업무', value: 'USR' },
];

export default function LoginScreen({ onLoggedIn }: Props) {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [userSe, setUserSe] = useState('USR');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleLogin() {
    setError(null);
    setLoading(true);
    try {
      const response = await login({ id, password, userSe });
      await saveToken(response.token);
      onLoggedIn(response);
    } catch (e) {
      setError(e instanceof Error ? e.message : '로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>egov-web-practice</Text>

      <View style={styles.tabs}>
        {USER_TYPES.map((t) => (
          <Pressable
            key={t.value}
            style={[styles.tab, userSe === t.value && styles.tabActive]}
            onPress={() => setUserSe(t.value)}
          >
            <Text style={[styles.tabText, userSe === t.value && styles.tabTextActive]}>{t.label}</Text>
          </Pressable>
        ))}
      </View>

      <TextInput
        style={styles.input}
        placeholder="아이디"
        autoCapitalize="none"
        value={id}
        onChangeText={setId}
      />
      <TextInput
        style={styles.input}
        placeholder="비밀번호"
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <Pressable style={styles.button} onPress={handleLogin} disabled={loading || !id || !password}>
        {loading ? <ActivityIndicator color="#fff" /> : <Text style={styles.buttonText}>로그인</Text>}
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f4f6fa', justifyContent: 'center', padding: 24 },
  title: { fontSize: 22, fontWeight: '700', textAlign: 'center', marginBottom: 24, color: '#20242c' },
  tabs: { flexDirection: 'row', marginBottom: 16, borderRadius: 8, overflow: 'hidden', borderWidth: 1, borderColor: '#e7e9ee' },
  tab: { flex: 1, paddingVertical: 10, alignItems: 'center', backgroundColor: '#fff' },
  tabActive: { backgroundColor: '#3a6df0' },
  tabText: { color: '#656b78', fontWeight: '600' },
  tabTextActive: { color: '#fff' },
  input: { backgroundColor: '#fff', borderWidth: 1, borderColor: '#e7e9ee', borderRadius: 8, padding: 12, marginBottom: 12, fontSize: 15 },
  button: { backgroundColor: '#3a6df0', borderRadius: 8, padding: 14, alignItems: 'center', marginTop: 8 },
  buttonText: { color: '#fff', fontWeight: '700', fontSize: 15 },
  error: { color: '#d0270b', marginBottom: 8, textAlign: 'center' },
});
