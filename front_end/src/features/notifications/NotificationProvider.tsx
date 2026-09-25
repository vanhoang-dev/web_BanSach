import { useEffect, useState } from 'react';

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

  useEffect(() => {
    const handleNotification = (event: Event) => {
      const { message, tone } = (event as CustomEvent<Omit<Notification, 'id'>>).detail;
      const id = Date.now() + Math.random();
      setNotifications((current) => [...current.slice(-2), { id, message, tone }]);
      window.setTimeout(() => {
        setNotifications((current) => current.filter((notification) => notification.id !== id));
      }, 4000);
    };

    window.addEventListener(notificationEvent, handleNotification);
    return () => window.removeEventListener(notificationEvent, handleNotification);
  }, []);

  return (
    <div className="pointer-events-none fixed inset-x-4 bottom-4 z-[100] flex flex-col items-end gap-2 sm:left-auto sm:w-96" aria-live="polite" aria-atomic="true">
      {notifications.map((notification) => (
        <div
          key={notification.id}
          role={notification.tone === 'error' ? 'alert' : 'status'}
          className={`w-full rounded-lg border px-4 py-3 text-sm font-semibold shadow-lg ${
            notification.tone === 'error'
              ? 'border-error/30 bg-error-container text-on-error-container'
              : notification.tone === 'success'
                ? 'border-emerald-200 bg-emerald-50 text-emerald-800'
                : 'border-primary/20 bg-primary-fixed text-on-primary-fixed'
          }`}
        >
          {notification.message}
        </div>
      ))}
    </div>
  );
}
