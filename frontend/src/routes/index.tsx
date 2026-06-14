import { Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';

function DashboardPlaceholder() {
  return <div style={{ padding: 36 }}><h2>Dashboard</h2><p>Coming soon...</p></div>;
}

function LoginPlaceholder() {
  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
      <h2>Login Page — Coming in Story 1.8</h2>
    </div>
  );
}

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPlaceholder />} />
      <Route path="/" element={<MainLayout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPlaceholder />} />
      </Route>
    </Routes>
  );
}
