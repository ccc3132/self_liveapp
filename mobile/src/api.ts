import * as SecureStore from 'expo-secure-store';

// 개발 중에는 PC의 LAN IP를 쓴다(휴대폰의 localhost는 휴대폰 자신을 가리키므로 동작하지 않는다).
// PC와 휴대폰이 같은 Wi-Fi에 있어야 하고, PC 방화벽에서 8080 포트(사설망)를 열어둬야 한다.
export const API_BASE_URL = 'http://192.168.68.102:8080';

const TOKEN_KEY = 'egov_jwt_token';

export async function saveToken(token: string) {
  await SecureStore.setItemAsync(TOKEN_KEY, token);
}

export async function getToken(): Promise<string | null> {
  return SecureStore.getItemAsync(TOKEN_KEY);
}

export async function clearToken() {
  await SecureStore.deleteItemAsync(TOKEN_KEY);
}

export type LoginRequest = { id: string; password: string; userSe: string };
export type LoginResponse = { token: string; id: string; name: string; orgnztNm: string | null; userSe: string };

export async function login(request: LoginRequest): Promise<LoginResponse> {
  const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  });
  if (!res.ok) {
    throw new Error(res.status === 401 ? '아이디 또는 비밀번호가 올바르지 않습니다.' : '로그인에 실패했습니다.');
  }
  return res.json();
}

async function authorizedGet(path: string) {
  const token = await getToken();
  const res = await fetch(`${API_BASE_URL}${path}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
  });
  if (res.status === 401 || res.status === 403) {
    await clearToken();
    throw new Error('로그인이 만료되었습니다. 다시 로그인해 주세요.');
  }
  if (!res.ok) {
    throw new Error('요청이 실패했습니다.');
  }
  return res.json();
}

export type DashboardResponse = {
  todaySchedules: Array<Record<string, unknown>>;
  recentNotices: Array<Record<string, unknown>>;
  unreadNoteCount: number;
  activeUsers: Array<{ loginId: string; name: string; loginAt: string }>;
};

export function fetchDashboard(): Promise<DashboardResponse> {
  return authorizedGet('/api/dashboard');
}

export function fetchMe(): Promise<LoginResponse> {
  return authorizedGet('/api/auth/me');
}
