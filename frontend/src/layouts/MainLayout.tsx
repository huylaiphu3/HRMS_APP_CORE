import { useState } from 'react';
import { Layout } from 'antd';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import ContentTopbar from './ContentTopbar';

const { Content } = Layout;

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(() => {
    return localStorage.getItem('sidebar_collapsed') === 'true';
  });

  const handleCollapse = (value: boolean) => {
    setCollapsed(value);
    localStorage.setItem('sidebar_collapsed', String(value));
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sidebar collapsed={collapsed} onCollapse={handleCollapse} />
      <Layout style={{ marginLeft: collapsed ? 72 : 260, transition: 'margin-left 0.2s' }}>
        <ContentTopbar />
        <Content style={{ padding: '28px 36px', background: '#F8FAFC' }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
