import { useCallback, useEffect, useState } from 'react';
import { RefreshControl, ScrollView, StyleSheet, Text, View } from 'react-native';
import { clearToken, fetchDashboard, type DashboardResponse, type LoginResponse } from './api';

type Props = { user: LoginResponse; onLoggedOut: () => void };

export default function DashboardScreen({ user, onLoggedOut }: Props) {
  const [dashboard, setDashboard] = useState<DashboardResponse | null>(null);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    try {
      setError(null);
      setDashboard(await fetchDashboard());
    } catch (e) {
      setError(e instanceof Error ? e.message : '불러오는 데 실패했습니다.');
      if (e instanceof Error && e.message.includes('만료')) {
        await clearToken();
        onLoggedOut();
      }
    }
  }, [onLoggedOut]);

  useEffect(() => {
    load();
  }, [load]);

  async function onRefresh() {
    setRefreshing(true);
    await load();
    setRefreshing(false);
  }

  async function handleLogout() {
    await clearToken();
    onLoggedOut();
  }

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.content}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
    >
      <View style={styles.header}>
        <View>
          <Text style={styles.title}>대시보드</Text>
          <Text style={styles.welcome}>{user.name}({user.orgnztNm ?? user.userSe})님</Text>
        </View>
        <Text style={styles.logout} onPress={handleLogout}>
          로그아웃
        </Text>
      </View>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <Card title="오늘 일정">
        {!dashboard || dashboard.todaySchedules.length === 0 ? (
          <Text style={styles.empty}>오늘 등록된 일정이 없습니다.</Text>
        ) : (
          dashboard.todaySchedules.map((s, i) => (
            <Text key={i} style={styles.row}>
              {String(s.schdul_nm ?? '')} ({String(s.schdul_place ?? '')})
            </Text>
          ))
        )}
      </Card>

      <Card title="최근 공지">
        {!dashboard || dashboard.recentNotices.length === 0 ? (
          <Text style={styles.empty}>등록된 공지가 없습니다.</Text>
        ) : (
          dashboard.recentNotices.map((n, i) => (
            <Text key={i} style={styles.row}>
              {String(n.ntt_sj ?? '')}
            </Text>
          ))
        )}
      </Card>

      <Card title="쪽지">
        <Text style={styles.row}>안 읽은 쪽지 {dashboard?.unreadNoteCount ?? 0}건</Text>
      </Card>

      <Card title={`현재 접속 중인 사용자 (${dashboard?.activeUsers.length ?? 0}명)`}>
        {!dashboard || dashboard.activeUsers.length === 0 ? (
          <Text style={styles.empty}>접속 중인 사용자가 없습니다.</Text>
        ) : (
          dashboard.activeUsers.map((u, i) => (
            <Text key={i} style={styles.row}>
              {u.name} ({u.loginId})
            </Text>
          ))
        )}
      </Card>
    </ScrollView>
  );
}

function Card({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <View style={styles.card}>
      <Text style={styles.cardTitle}>{title}</Text>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f4f6fa' },
  content: { padding: 20, paddingTop: 56 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 },
  title: { fontSize: 21, fontWeight: '700', color: '#20242c' },
  welcome: { color: '#656b78', marginTop: 2 },
  logout: { color: '#3a6df0', fontWeight: '600' },
  error: { color: '#d0270b', marginBottom: 12 },
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: '#e7e9ee',
  },
  cardTitle: { fontSize: 14, fontWeight: '700', color: '#2a2d34', marginBottom: 10 },
  row: { fontSize: 13.5, color: '#20242c', paddingVertical: 4 },
  empty: { fontSize: 13, color: '#9aa0ab' },
});
