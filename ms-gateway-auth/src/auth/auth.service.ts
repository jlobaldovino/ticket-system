import { Injectable, UnauthorizedException } from '@nestjs/common';
import { UsersService } from '../users/users.service';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcryptjs';

@Injectable()
export class AuthService {
  constructor(
    private readonly usersService: UsersService,
    private readonly jwtService: JwtService,
  ) {}

  async validateUser(email: string, password: string) {
    const user = await this.usersService.obtenerPorEmail(email);
    const passwordValida = await bcrypt.compare(password, user.passwordHash);
    if (!passwordValida) {throw new UnauthorizedException('Credenciales incorrectas');}
    return {sub: user.id,email: user.email,rol: user.rol,};
  }

  async login(email: string, password: string) {
    const payload = await this.validateUser(email, password);
    const token = this.jwtService.sign(payload);
    return { accessToken: token };
  }
}
