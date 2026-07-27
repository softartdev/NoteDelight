import { initializeApp } from "firebase/app";
import { getAnalytics, isSupported } from "firebase/analytics";

const firebaseConfig = {
  apiKey: "AIzaSyB1Tx2XNQtH3rqM31PWRMHT9fd1Jfd56uc",
  authDomain: "noteroom-3ba13.firebaseapp.com",
  databaseURL: "https://noteroom-3ba13.firebaseio.com",
  projectId: "noteroom-3ba13",
  storageBucket: "noteroom-3ba13.firebasestorage.app",
  messagingSenderId: "936732740110",
  appId: "1:936732740110:web:eb43c57d566fa140474a5f",
  measurementId: "G-4PN6QFN6YX"
};

export default function initializeFirebase() {
  const app = initializeApp(firebaseConfig);
  isSupported().then((supported) => {
    if (supported) getAnalytics(app);
  });
}
