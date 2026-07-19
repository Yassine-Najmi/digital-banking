import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatMessage } from '../models/chat.model';
import { ChatService } from '../services/chat.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './chatbot.component.html',
  styleUrl: './chatbot.component.css'
})
export class ChatbotComponent {
  messages: ChatMessage[] = [
    {
      role: 'assistant',
      content: 'Bonjour, je suis le conseiller virtuel E-Bank. Posez-moi une question sur nos produits, frais ou procédures.'
    }
  ];
  question = '';
  loading = false;
  errorMessage = '';

  constructor(private chatService: ChatService) {}

  send(): void {
    const text = this.question.trim();
    if (!text || this.loading) {
      return;
    }

    this.messages.push({ role: 'user', content: text });
    this.question = '';
    this.loading = true;
    this.errorMessage = '';

    this.chatService.ask(text).subscribe({
      next: (response) => {
        this.messages.push({ role: 'assistant', content: response.answer });
        this.loading = false;
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Impossible d\'obtenir une réponse pour le moment.';
        this.messages.push({
          role: 'assistant',
          content: 'Désolé, une erreur est survenue. Réessayez dans un instant.'
        });
        this.loading = false;
      }
    });
  }
}
