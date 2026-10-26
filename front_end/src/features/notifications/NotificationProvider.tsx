import { useEffect, useRef, useState } from 'react';

import { Icon } from '@/components/ui/staticUi';

type NotificationTone = 'success' | 'error' | 'info';

type Notification = {
  id: number;
  message: string;
  tone: NotificationTone;
};

const notificationEvent = 'app:notification';

const publish = (message: string, tone: NotificationTone) => {
  window.dispatchEvent(new CustomEvent(notificationEvent, { detail: { message, tone } }));
};

export const notify = {
  success: (message: string) => publish(message, 'success'),
  error: (message: string) => publish(message, 'error'),
  info: (message: string) => publish(message, 'info'),
};

export function NotificationProvider() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const timers = useRef<number[]>([]);

  useEffect(() => {
    const handleNotification = (event: Event) => {
      const { message, tone } = (event as CustomEvent<Omit<Notification, 'id'>>).detail;
      const id = Date.now() + Math.random();
      setNotifications((current) => [...current.slice(-2), { id, message, tone }]);
      const timer = window.setTimeout(() => {
        setNotifications((current) => current.filter((notification) => notification.id !== id));
      }, 4500);
      timers.current.push(timer);
    };

    window.addEventListener(notificationEvent, handleNotification);
    return () => {
      window.removeEventListener(notificationEvent, handleNotification);
      timers.current.forEach(window.clearTimeout);
    };
  }, []);

  const dismiss = (id: number) => setNotifications((current) => current.filter((notification) => notification.id !== id));

  const toneStyles = {
    error: { wrap: 'border-red-200 bg-white', icon: 'bg-red-50 text-error', title: 'Không thể thực hiện' },
    success: { wrap: 'border-emerald-200 bg-white', icon: 'bg-emerald-50 text-emerald-700', title: 'Thao tác thành công' },
    info: { wrap: 'border-primary/20 bg-white', icon: 'bg-primary-fixed text-primary', title: 'Thông báo' },
  };

  return (
    <div className="pointer-events-none fixed inset-x-4 top-20 z-[100] flex flex-col items-end gap-2 sm:left-auto sm:right-5 sm:w-[380px]" aria-live="polite">
      {notifications.map((notification) => {
        const style = toneStyles[notification.tone];
        return (
        <div
          key={notification.id}
          role={notification.tone === 'error' ? 'alert' : 'status'}
          className={`pointer-events-auto flex w-full items-start gap-3 rounded-lg border p-3.5 text-sm shadow-lg shadow-primary/10 ${style.wrap}`}
        >
          <span className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-md ${style.icon}`}><Icon name={notification.tone === 'success' ? 'check' : notification.tone === 'error' ? 'warning' : 'bell'} className="h-5 w-5" /></span>
          <span className="min-w-0 flex-1"><strong className="block text-sm text-on-surface">{style.title}</strong><span className="mt-0.5 block leading-5 text-on-surface-variant">{notification.message}</span></span>
          <button type="button" onClick={() => dismiss(notification.id)} aria-label="Đóng thông báo" className="flex h-8 w-8 shrink-0 items-center justify-center rounded-md text-on-surface-variant hover:bg-surface-container"><Icon name="x" className="h-4 w-4" /></button>
        </div>
      );})}
    </div>
  );
}
