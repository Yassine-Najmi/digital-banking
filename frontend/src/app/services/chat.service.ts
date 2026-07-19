import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ChatRequestDTO, ChatResponseDTO } from '../models/chat.model';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private readonly url = `${environment.backendHost}/chat`;

  constructor(private http: HttpClient) {}

  ask(question: string): Observable<ChatResponseDTO> {
    const body: ChatRequestDTO = { question };
    return this.http.post<ChatResponseDTO>(this.url, body);
  }
}
